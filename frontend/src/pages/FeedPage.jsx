import { useEffect, useState } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import PostForm from '../components/PostForm'
import PostCard from '../components/PostCard'
import { fetchStream, createPost } from '../services/api'
import './FeedPage.css'

export default function FeedPage() {
  const { getAccessTokenSilently, isAuthenticated, user } = useAuth0()
  const [posts, setPosts] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let isMounted = true

    const loadPosts = async () => {
      try {
        setError(null)
        setLoading(true)

        const streamPosts = await fetchStream()

        if (isMounted) {
          setPosts(streamPosts)
        }
      } catch (err) {
        if (!isMounted) return

        const status = err?.status ?? err?.response?.status

        if (status === 401 || status === 403) {
          setError('You need to log in')
        } else {
          setError('Error loading posts')
        }
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    loadPosts()

    return () => {
      isMounted = false
    }
  }, [])

  const handlePostCreated = async (content) => {
    if (!isAuthenticated) {
      setError('You need to log in')
      return
    }

    try {
      setError(null)
      const token = await getAccessTokenSilently()
      const newPost = await createPost(content, token, user)
      setPosts((prev) => [newPost, ...prev])
    } catch {
      setError('Could not create post. Make sure you are logged in.')
    }
  }

  return (
    <div className="feed-page">
      <h1 className="feed-title">Home</h1>

      <PostForm onPostCreated={handlePostCreated} />

      {error && <p className="feed-error">{error}</p>}

      <div className="feed-list">
        {loading ? (
          <p>Loading...</p>
        ) : posts.length === 0 ? (
          <p className="feed-empty">No posts yet. Be the first!</p>
        ) : (
          posts.map((post) => (
            <PostCard key={post.id} post={post} />
          ))
        )}
      </div>
    </div>
  )
}
