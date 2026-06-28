import React, { useState, useRef, useEffect } from 'react';
import { Card, Form, Button, Spinner, Badge } from 'react-bootstrap';
import { chatApi } from '../services/api';

function Chat() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [sessionId, setSessionId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [domainCode, setDomainCode] = useState('CORE');
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || loading) return;

    const userMsg = { role: 'USER', content: input, createdAt: new Date().toISOString() };
    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      console.log('Sending chat message:', { input, sessionId, domainCode });
      const { data } = await chatApi.send(input, sessionId, domainCode);
      console.log('Chat response received:', data);
      setSessionId(data.id);
      setMessages(data.messages.map((m) => ({
        role: m.role,
        content: m.content,
        createdAt: m.createdAt,
      })));
    } catch (err) {
      console.error('Chat error:', err);
      console.error('Error response:', err.response);
      setMessages((prev) => [...prev, {
        role: 'ASSISTANT',
        content: `Error: ${err.response?.data?.message || err.message || 'Please try again.'}`,
        createdAt: new Date().toISOString(),
      }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-1">AI Chat</h2>
          <p className="text-muted mb-0">DB AI Decision Intelligence Platform (DAIP)</p>
        </div>
        <Form.Select
          style={{ width: 200 }}
          value={domainCode}
          onChange={(e) => setDomainCode(e.target.value)}
        >
          <option value="CORE">Core</option>
          <option value="RISK">Risk</option>
          <option value="COMPLIANCE">Compliance</option>
          <option value="ESG">ESG</option>
          <option value="AML">AML</option>
        </Form.Select>
      </div>

      <Card className="stat-card">
        <Card.Body className="chat-container">
          <div className="chat-messages">
            {messages.length === 0 && (
              <div className="text-center text-muted py-5">
                <div className="fs-1 mb-3">🤖</div>
                <p>Ask about risk, compliance, documents, or platform capabilities.</p>
                <Badge bg="info">Domain: {domainCode}</Badge>
              </div>
            )}
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`chat-bubble ${msg.role === 'USER' ? 'user' : 'assistant'}`}
              >
                {msg.content}
              </div>
            ))}
            {loading && (
              <div className="chat-bubble assistant">
                <Spinner size="sm" /> Thinking...
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          <Form onSubmit={handleSend} className="mt-3 d-flex gap-2">
            <Form.Control
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Type your message..."
              disabled={loading}
            />
            <Button
              type="submit"
              disabled={loading || !input.trim()}
              style={{ backgroundColor: '#0018A8', borderColor: '#0018A8' }}
            >
              Send
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
}

export default Chat;
