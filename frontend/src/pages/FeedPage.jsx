import { useState } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import PostForm from '../components/PostForm'
import PostCard from '../components/PostCard'
import { fetchStream, createPost } from '../services/api'
import './FeedPage.css'

const MOCK_POSTS = [
  { id: 1, content: 'Welcome to Secure Twitter! 🎉', authorId: 'demo', authorName: 'Demo User', createdAt: new Date().toISOString() },
  { id: 2, content: 'This feed will show real posts once the backend is connected.', authorId: 'demo', authorName: 'Demo User', createdAt: new Date(Date.now() - 60000).toISOString() },
]

export default function FeedPage() {
  const { getAccessTokenSilently, isAuthenticated } = useAuth0()
  const [posts, setPosts] = useState(MOCK_POSTS)
  const [error, setError] = useState(null)

  const handlePostCreated = async (content) => {
    try {
      setError(null)
      const token = await getAccessTokenSilently()
      const newPost = await createPost(content, token)
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
        {posts.length === 0
          ? <p className="feed-empty">No posts yet. Be the first!</p>
          : posts.map((post) => <PostCard key={post.id} post={post} />)
        }
      </div>
    </div>
  )
}
