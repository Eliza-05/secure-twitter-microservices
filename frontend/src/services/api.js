const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export async function fetchStream() {
  const res = await fetch(`${BASE_URL}/api/stream`)
  if (!res.ok) throw new Error('Failed to fetch stream')
  return res.json()
}

export async function createPost(content, accessToken) {
  const res = await fetch(`${BASE_URL}/api/posts`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({ content }),
  })
  if (!res.ok) throw new Error('Failed to create post')
  return res.json()
}

export async function fetchMe(accessToken) {
  const res = await fetch(`${BASE_URL}/api/me`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
  if (!res.ok) throw new Error('Failed to fetch user info')
  return res.json()
}
