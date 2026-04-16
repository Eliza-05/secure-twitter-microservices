import { useAuth0 } from '@auth0/auth0-react'
import './Navbar.css'

export default function Navbar() {
  const { isAuthenticated, user, loginWithRedirect, logout } = useAuth0()

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <span className="navbar-brand">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
            <path d="M23 3a10.9 10.9 0 01-3.14 1.53A4.48 4.48 0 0022.43 1a9 9 0 01-2.88 1.1A4.52 4.52 0 0016 0c-2.5 0-4.5 2-4.5 4.5 0 .35.04.7.1 1.03C7.73 5.33 4.1 3.58 1.67.9a4.5 4.5 0 00-.61 2.27c0 1.56.8 2.94 2 3.75A4.47 4.47 0 011 6.18v.05c0 2.18 1.55 4 3.6 4.41a4.5 4.5 0 01-2.03.08c.57 1.79 2.23 3.09 4.2 3.13A9.05 9.05 0 010 15.54 12.76 12.76 0 006.93 17.5c8.32 0 12.86-6.9 12.86-12.87 0-.2 0-.39-.01-.58A9.17 9.17 0 0023 3z"/>
          </svg>
          Secure Twitter
        </span>

        <div className="navbar-actions">
          {isAuthenticated ? (
            <>
              <span className="navbar-user">
                {user?.picture && (
                  <img src={user.picture} alt={user.name} className="navbar-avatar" />
                )}
                {user?.name}
              </span>
              <button
                className="btn btn-outline"
                onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
              >
                Log out
              </button>
            </>
          ) : (
            <button className="btn btn-primary" onClick={() => loginWithRedirect()}>
              Log in
            </button>
          )}
        </div>
      </div>
    </nav>
  )
}
