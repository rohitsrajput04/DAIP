import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(username, password);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <Card className="login-card">
        <Card.Body className="p-4">
          <div className="text-center mb-4">
            <h2 className="fw-bold" style={{ color: '#0018A8' }}>DAIP</h2>
            <p className="text-muted">DB AI Decision Intelligence Platform (DAIP)</p>
          </div>

          {error && <Alert variant="danger">{error}</Alert>}

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
                required
              />
            </Form.Group>
            <Form.Group className="mb-4">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
                required
              />
            </Form.Group>
            <Button
              type="submit"
              variant="primary"
              className="w-100"
              disabled={loading}
              style={{ backgroundColor: '#0018A8', borderColor: '#0018A8' }}
            >
              {loading ? <Spinner size="sm" /> : 'Sign In'}
            </Button>
          </Form>

          <div className="mt-4 p-3 bg-light rounded small">
            <strong>Demo accounts:</strong>
            <div>admin / admin123</div>
            <div>analyst / analyst123</div>
            <div>compliance / compliance123</div>
          </div>
        </Card.Body>
      </Card>
    </div>
  );
}

export default Login;
