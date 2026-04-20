import { useState } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import './PostForm.css'

const MAX_CHARS = 140

export default function PostForm({ onPostCreated }) {
  const { isAuthenticated, loginWithRedirect } = useAuth0()
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(false)

  const remaining = MAX_CHARS - content.length
  const isOverLimit = remaining < 0
  const isEmpty = content.trim().length === 0

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (isEmpty || isOverLimit || loading) return
    setLoading(true)
    try {
      await onPostCreated(content.trim())
      setContent('')
    } finally {
      setLoading(false)
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="post-form post-form--guest">
        <p>Join the conversation</p>
        <button className="btn-post" onClick={() => loginWithRedirect()}>
          Log in to post
        </button>
      </div>
    )
  }

  return (
    <form className="post-form" onSubmit={handleSubmit}>
      <textarea
        className="post-form__textarea"
        placeholder="What's happening? (max 140 characters)"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        rows={3}
        disabled={loading}
      />
      <div className="post-form__footer">
        <span className={`post-form__counter ${remaining <= 20 ? 'warn' : ''} ${isOverLimit ? 'error' : ''}`}>
          {remaining}
        </span>
        <button
          type="submit"
          className="btn-post"
          disabled={isEmpty || isOverLimit || loading}
        >
          {loading ? 'Posting…' : 'Post'}
        </button>
      </div>
    </form>
  )
}
