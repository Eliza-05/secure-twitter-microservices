import { useAuth0 } from '@auth0/auth0-react'

function App() {
  const { isLoading } = useAuth0()

  if (isLoading) return <div>Loading...</div>

  return (
    // TODO: add router and pages
    <div>Secure Twitter</div>
  )
}

export default App
