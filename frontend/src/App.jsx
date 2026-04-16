import { useAuth0 } from '@auth0/auth0-react'
import Navbar from './components/Navbar'
import FeedPage from './pages/FeedPage'
import './App.css'

function App() {
  const { isLoading } = useAuth0()

  if (isLoading) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
      </div>
    )
  }

  return (
    <div className="app">
      <Navbar />
      <main className="main-content">
        <FeedPage />
      </main>
    </div>
  )
}

export default App
