import React, { useState } from 'react';
import { Card, Form, Button, Spinner, Alert, Badge, ListGroup } from 'react-bootstrap';
import { searchApi } from '../services/api';

function Search() {
  const [query, setQuery] = useState('');
  const [domainCode, setDomainCode] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [searched, setSearched] = useState(false);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;

    setLoading(true);
    setError('');
    setSearched(true);

    try {
      const { data } = await searchApi.search(query, domainCode || null);
      setResults(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Search failed.');
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="mb-4">
        <h2 className="mb-1">Semantic Search</h2>
        <p className="text-muted mb-0">DB AI Decision Intelligence Platform (DAIP) — RAG document search</p>
      </div>

      <Card className="stat-card mb-4">
        <Card.Body>
          <Form onSubmit={handleSearch} className="row g-3 align-items-end">
            <div className="col-md-7">
              <Form.Label>Search query</Form.Label>
              <Form.Control
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="e.g. AML reporting requirements, credit risk limits..."
              />
            </div>
            <div className="col-md-3">
              <Form.Label>Domain filter</Form.Label>
              <Form.Select value={domainCode} onChange={(e) => setDomainCode(e.target.value)}>
                <option value="">All domains</option>
                <option value="CORE">Core</option>
                <option value="RISK">Risk</option>
                <option value="COMPLIANCE">Compliance</option>
                <option value="ESG">ESG</option>
                <option value="AML">AML</option>
              </Form.Select>
            </div>
            <div className="col-md-2">
              <Button
                type="submit"
                className="w-100"
                disabled={loading}
                style={{ backgroundColor: '#0018A8', borderColor: '#0018A8' }}
              >
                {loading ? <Spinner size="sm" /> : 'Search'}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>

      {error && <Alert variant="danger">{error}</Alert>}

      {searched && !loading && results.length === 0 && !error && (
        <Alert variant="info">No matching chunks found. Upload and index documents first.</Alert>
      )}

      {results.length > 0 && (
        <Card className="stat-card">
          <Card.Header className="bg-white fw-bold">
            Results ({results.length})
          </Card.Header>
          <ListGroup variant="flush">
            {results.map((result) => (
              <ListGroup.Item key={result.chunkId}>
                <div className="d-flex justify-content-between mb-2">
                  <strong>{result.fileName}</strong>
                  <div>
                    <Badge bg="secondary" className="me-1">{result.domainCode}</Badge>
                    {result.similarity != null && (
                      <Badge bg="info">{Math.round(result.similarity * 100)}% match</Badge>
                    )}
                  </div>
                </div>
                <p className="mb-0 text-muted small">{result.content}</p>
              </ListGroup.Item>
            ))}
          </ListGroup>
        </Card>
      )}
    </div>
  );
}

export default Search;
