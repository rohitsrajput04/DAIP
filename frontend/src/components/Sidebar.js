import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { Nav, Button } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { path: '/dashboard', label: 'Dashboard', icon: '📊' },
  { path: '/chat', label: 'AI Chat', icon: '🤖' },
  { path: '/search', label: 'Semantic Search', icon: '🔍' },
  { path: '/documents', label: 'Documents', icon: '📄' },
];

function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="daip-sidebar d-flex flex-column">
      <div className="p-4 border-bottom border-secondary">
        <h4 className="daip-brand mb-0">DAIP</h4>
        <small className="text-muted">DB AI Decision Intelligence Platform</small>
      </div>

      <Nav className="flex-column mt-3 flex-grow-1">
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `daip-nav-link text-decoration-none d-flex align-items-center ${isActive ? 'active' : ''}`
            }
          >
            <span className="me-2">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </Nav>

      <div className="p-3 border-top border-secondary">
        <div className="text-white-50 small mb-2">{user?.fullName}</div>
        <div className="text-white-50 small mb-3">{user?.role?.replace('_', ' ')}</div>
        <Button variant="outline-light" size="sm" onClick={handleLogout} className="w-100">
          Logout
        </Button>
      </div>
    </div>
  );
}

export default Sidebar;
