import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Spinner, Badge } from 'react-bootstrap';
import { dashboardApi } from '../services/api';
import { useAuth } from '../context/AuthContext';

function Dashboard() {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    dashboardApi.getMetrics()
      .then(({ data }) => setMetrics(data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" />
      </div>
    );
  }

  const stats = [
    { label: 'Total Documents', value: metrics?.totalDocuments, icon: '📄', color: '#0018A8' },
    { label: 'Chat Sessions', value: metrics?.totalChatSessions, icon: '💬', color: '#00A3E0' },
    { label: 'Platform Users', value: metrics?.totalUsers, icon: '👥', color: '#6f42c1' },
    { label: 'Uploaded Today', value: metrics?.documentsUploadedToday, icon: '📤', color: '#198754' },
  ];

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-1">Dashboard</h2>
          <p className="text-muted mb-0">Welcome back, {user?.fullName}</p>
        </div>
        <Badge bg="success" className="fs-6">{metrics?.platformStatus}</Badge>
      </div>

      <Row className="g-4 mb-4">
        {stats.map((stat) => (
          <Col md={3} key={stat.label}>
            <Card className="stat-card h-100">
              <Card.Body>
                <div className="d-flex justify-content-between">
                  <div>
                    <div className="text-muted small">{stat.label}</div>
                    <div className="fs-2 fw-bold" style={{ color: stat.color }}>{stat.value ?? 0}</div>
                  </div>
                  <div className="fs-2">{stat.icon}</div>
                </div>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>

      <Row className="g-4">
        <Col md={8}>
          <Card className="stat-card">
            <Card.Header className="bg-white fw-bold">Platform Overview</Card.Header>
            <Card.Body>
              <p>
                The DB AI Decision Intelligence Platform (DAIP) provides modular AI-powered
                decision support for investment banking operations including Risk, Compliance,
                ESG, AML, and Treasury domains.
              </p>
              <p className="mb-0">
                <strong> Active Domain:</strong> {metrics?.activeDomain} |
                <strong> Phase:</strong> 2 — RAG (Extract, Chunk, Embed, Semantic Search)
              </p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="stat-card">
            <Card.Header className="bg-white fw-bold">Quick Actions</Card.Header>
            <Card.Body>
              <ul className="list-unstyled mb-0">
                <li className="mb-2">🤖 Start an AI Chat session</li>
                <li className="mb-2">📄 Upload compliance documents</li>
                <li className="mb-2">🔍 Semantic search over indexed documents</li>
                <li>📊 Review platform metrics</li>
              </ul>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default Dashboard;
