import SockJS from 'sockjs-client/dist/sockjs'
import { Client } from '@stomp/stompjs'
import { API_BASE_URL } from './api'

let client = null
let subscription = null

export function connectToRoom(roomId, onMessage, onStatusChange) {
    disconnectWs()

    client = new Client({
        webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
        reconnectDelay: 5000,
        onConnect: () => {
            onStatusChange?.('connected')
            subscription = client.subscribe(`/topic/rooms/${roomId}`, frame => {
                try {
                    onMessage(JSON.parse(frame.body))
                } catch {
                    onMessage({
                        senderUsername: 'system',
                        content: frame.body,
                        createdAt: new Date().toISOString()
                    })
                }
            })
        },
        onStompError: frame => {
            onStatusChange?.(frame.headers?.message || 'stomp error')
        },
        onWebSocketError: () => {
            onStatusChange?.('websocket error')
        }
    })

    onStatusChange?.('connecting')
    client.activate()
}

export function sendRoomMessage(roomId, content) {
    if (!client?.connected) {
        throw new Error('WebSocket is not connected')
    }

    client.publish({
        destination: `/app/rooms/${roomId}/send`,
        body: JSON.stringify({ content })
    })
}

export async function disconnectWs() {
    if (subscription) {
        subscription.unsubscribe()
        subscription = null
    }

    if (client) {
        const activeClient = client
        client = null
        await activeClient.deactivate()
    }
}