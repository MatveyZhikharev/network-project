const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

async function request(path, options = {}) {
    const response = await fetch(`${API_BASE_URL}${path}`, {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        },
        ...options
    })

    const text = await response.text()
    const data = text ? safeJsonParse(text) : null

    if (!response.ok) {
        throw new Error(data?.message || data?.error || `HTTP ${response.status}`)
    }

    return data
}

function safeJsonParse(value) {
    try {
        return JSON.parse(value)
    } catch {
        return value
    }
}

export const authApi = {
    register(payload) {
        return request('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify(payload)
        })
    },
    login(payload) {
        return request('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify(payload)
        })
    },
    me() {
        return request('/api/auth/me')
    }
}

export const roomsApi = {
    getAll() {
        return request('/api/rooms')
    },
    create(payload) {
        return request('/api/rooms', {
            method: 'POST',
            body: JSON.stringify(payload)
        })
    },
    join(roomId, payload) {
        return request(`/api/rooms/${roomId}/join`, {
            method: 'POST',
            body: JSON.stringify(payload ?? { password: null })
        })
    },
    members(roomId) {
        return request(`/api/rooms/${roomId}/members`)
    },
    getMessages(roomId) {
        return request(`/api/rooms/${roomId}/messages`)
    },
}

export { API_BASE_URL }