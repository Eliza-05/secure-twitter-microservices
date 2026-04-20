import './PostCard.css'

export default function PostCard({ post }) {
  const date = new Date(post.createdAt).toLocaleString('en-US', {
    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
  const authorName = post.authorName ?? '?'
  const authorPicture = post.authorPicture

  return (
    <article className="post-card">
      {authorPicture ? (
        <img
          className="post-avatar post-avatar--image"
          src={authorPicture}
          alt={`${authorName} avatar`}
        />
      ) : (
        <div className="post-avatar">
          {authorName[0]?.toUpperCase() ?? '?'}
        </div>
      )}
      <div className="post-body">
        <div className="post-header">
          <span className="post-author">{authorName}</span>
          <span className="post-date">{date}</span>
        </div>
        <p className="post-content">{post.content}</p>
      </div>
    </article>
  )
}
