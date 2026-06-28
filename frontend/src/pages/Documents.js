import React, { useState, useEffect, useRef } from 'react';
import { Card, Table, Button, Form, Alert, Spinner, Badge, Row, Col } from 'react-bootstrap';
import { documentApi } from '../services/api';

function statusVariant(status) {
  switch (status) {
    case 'INDEXED': return 'success';
    case 'PROCESSING': return 'warning';
    case 'FAILED': return 'danger';
    default: return 'info';
  }
}

function Documents() {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [domainCode, setDomainCode] = useState('CORE');
  const [description, setDescription] = useState('');
  const fileInputRef = useRef(null);

  const loadDocuments = () => {
    setLoading(true);
    documentApi.getAll()
      .then(({ data }) => setDocuments(data))
      .catch(() => setError('Failed to load documents'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadDocuments();
    const interval = setInterval(loadDocuments, 5000);
    return () => clearInterval(interval);
  }, []);

  const handleUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    setError('');
    setSuccess('');

    const formData = new FormData();
    formData.append('file', file);
    formData.append('domainCode', domainCode);
    if (description) formData.append('description', description);

    try {
      await documentApi.upload(formData);
      setSuccess(`"${file.name}" uploaded — extraction and RAG indexing started.`);
      setDescription('');
      loadDocuments();
    } catch (err) {
      setError(err.response?.data?.message || 'Upload failed.');
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const handleReprocess = async (id) => {
    try {
      await documentApi.reprocess(id);
      setSuccess('Document reprocessing started.');
      loadDocuments();
    } catch (err) {
      setError(err.response?.data?.message || 'Reprocess failed.');
    }
  };

  const formatSize = (bytes) => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1048576) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / 1048576).toFixed(1)} MB`;
  };

  return (
    <div>
      <div className="mb-4">
        <h2 className="mb-1">Documents</h2>
        <p className="text-muted mb-0">Upload PDF, Word, Excel — auto extract, chunk, embed (pgvector)</p>
      </div>

      {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}
      {success && <Alert variant="success" dismissible onClose={() => setSuccess('')}>{success}</Alert>}

      <Card className="stat-card mb-4">
        <Card.Body>
          <Row className="g-3 mb-3">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Domain</Form.Label>
                <Form.Select value={domainCode} onChange={(e) => setDomainCode(e.target.value)}>
                  <option value="CORE">Core</option>
                  <option value="RISK">Risk</option>
                  <option value="COMPLIANCE">Compliance</option>
                  <option value="ESG">ESG</option>
                  <option value="AML">AML</option>
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={8}>
              <Form.Group>
                <Form.Label>Description (optional)</Form.Label>
                <Form.Control
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Brief document description"
                />
              </Form.Group>
            </Col>
          </Row>

          <div className="upload-zone" onClick={() => fileInputRef.current?.click()}>
            {uploading ? (
              <Spinner animation="border" />
            ) : (
              <>
                <div className="fs-1 mb-2">📤</div>
                <p className="mb-0">Click to upload PDF, Word (.doc/.docx), Excel (.xls/.xlsx), or Text</p>
                <small className="text-muted">Max 50MB — DAIP RAG pipeline runs automatically after upload</small>
              </>
            )}
            <input
              ref={fileInputRef}
              type="file"
              hidden
              onChange={handleUpload}
              accept=".pdf,.doc,.docx,.xls,.xlsx,.txt,.csv"
            />
          </div>
        </Card.Body>
      </Card>

      <Card className="stat-card">
        <Card.Header className="bg-white fw-bold d-flex justify-content-between">
          <span>Uploaded Documents</span>
          <Button variant="outline-secondary" size="sm" onClick={loadDocuments}>Refresh</Button>
        </Card.Header>
        <Card.Body className="p-0">
          {loading ? (
            <div className="text-center py-4"><Spinner animation="border" /></div>
          ) : documents.length === 0 ? (
            <div className="text-center text-muted py-4">No documents uploaded yet.</div>
          ) : (
            <Table responsive hover className="mb-0">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>Domain</th>
                  <th>Size</th>
                  <th>Status</th>
                  <th>Chunks</th>
                  <th>Uploaded By</th>
                  <th>Date</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {documents.map((doc) => (
                  <tr key={doc.id}>
                    <td>
                      {doc.fileName}
                      {doc.processingError && (
                        <div className="text-danger small">{doc.processingError}</div>
                      )}
                    </td>
                    <td><Badge bg="secondary">{doc.domainCode}</Badge></td>
                    <td>{formatSize(doc.fileSize)}</td>
                    <td><Badge bg={statusVariant(doc.status)}>{doc.status}</Badge></td>
                    <td>{doc.chunkCount ?? 0}</td>
                    <td>{doc.uploadedBy}</td>
                    <td>{new Date(doc.uploadedAt).toLocaleDateString()}</td>
                    <td>
                      <Button
                        variant="outline-primary"
                        size="sm"
                        onClick={() => handleReprocess(doc.id)}
                        disabled={doc.status === 'PROCESSING'}
                      >
                        Reindex
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>
    </div>
  );
}

export default Documents;
