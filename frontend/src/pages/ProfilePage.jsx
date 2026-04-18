import { useEffect, useState } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import { fetchMe } from '../services/api'

export default function ProfilePage() {
  const { isAuthenticated, getAccessTokenSilently, user: auth0User } = useAuth0()

  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let isMounted = true

    const loadProfile = async () => {
      try {
        setError(null)

        if (!isAuthenticated) {
          if (isMounted) {
            setError('You need to log in')
            setLoading(false)
          }
          return
        }

        const token = await getAccessTokenSilently()
        const profile = await fetchMe(token)

        if (isMounted) {
          setUser(profile)
        }
      } catch (err) {
        if (!isMounted) return

        const status = err?.status ?? err?.response?.status
        const authError = err?.error

        if (
          status === 401 ||
          status === 403 ||
          authError === 'login_required' ||
          authError === 'consent_required'
        ) {
          setError('You need to log in')
        } else {
          setError('Error loading profile')
        }
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    loadProfile()

    return () => {
      isMounted = false
    }
  }, [isAuthenticated, getAccessTokenSilently])

  // 🔥 Fallback inteligente (backend vs Auth0)
  const finalUser = {
    name:
      user?.name && !user.name.includes('|')
        ? user.name
        : auth0User?.name,
    email: user?.email || auth0User?.email,
    picture: user?.picture ?? auth0User?.picture,
  }

  const avatar = finalUser.picture

  if (loading) {
    return (
      <div>
        <h1>Profile</h1>
        <p>Loading profile...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div>
        <h1>Profile</h1>
        <p>{error}</p>
      </div>
    )
  }

  return (
    <div>
      <h1>Profile</h1>

      {avatar && (
        <img
          src={avatar}
          alt={finalUser.name ? `${finalUser.name} avatar` : 'Profile avatar'}
          width="96"
          height="96"
          style={{ borderRadius: '50%' }}
        />
      )}

      <p>Name: {finalUser.name ?? 'N/A'}</p>
      <p>Email: {finalUser.email ?? 'N/A'}</p>
    </div>
  )
}