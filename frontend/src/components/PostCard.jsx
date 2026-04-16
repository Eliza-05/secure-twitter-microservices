import './PostCard.css'

export default function PostCard({ post }) {
  const date = new Date(post.createdAt).toLocaleString('en-US', {
    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })

  return (
    <article className="post-card">
      <div className="post-avatar">
        {post.authorName?.[0]?.toUpperCase() ?? '?'}
      </div>
      <div className="post-body">
        <div className="post-header">
          <span className="post-author">{post.authorName ?? post.authorId}</span>
          <span className="post-date">{date}</span>
        </div>
        <p className="post-content">{post.content}</p>
      </div>
    </article>
  )
}
