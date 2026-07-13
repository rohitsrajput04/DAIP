import React, { useState, useRef, useEffect } from 'react';
import { Card, Form, Button, Spinner, Badge, ToggleButton, ToggleButtonGroup } from 'react-bootstrap';
import { chatApi } from '../services/api';
import SpeechButton from '../components/SpeechButton';

function Chat() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [sessionId, setSessionId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [domainCode, setDomainCode] = useState('CORE');
  const [autoSpeak, setAutoSpeak] = useState(false);
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
      
      // Append the assistant's response to existing messages instead of replacing all
      const assistantMsg = data.messages.find(m => m.role === 'ASSISTANT');
      if (assistantMsg) {
        setMessages((prev) => [...prev, {
          role: assistantMsg.role,
          content: assistantMsg.content,
          createdAt: assistantMsg.createdAt,
        }]);
      }
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

  const handleTranscript = (transcript) => {
    setInput(transcript);
  };

  const handleSpeak = (text) => {
    if (autoSpeak && text) {
      // Text-to-speech is handled by the SpeechButton component
    }
  };

  // Get the last assistant message for text-to-speech
  const lastAssistantMessage = messages.filter(m => m.role === 'ASSISTANT').pop();

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-1">AI Chat</h2>
          <p className="text-muted mb-0">DB AI Decision Intelligence Platform (DAIP)</p>
        </div>
        <div className="d-flex align-items-center gap-3">
          <ToggleButtonGroup
            type="checkbox"
            value={[autoSpeak ? 'speak' : '']}
            onChange={(val) => setAutoSpeak(val.includes('speak'))}
          >
            <ToggleButton id="tbg-speak" value="speak" variant={autoSpeak ? 'success' : 'outline-success'} size="sm">
              🔊 Auto-speak
            </ToggleButton>
          </ToggleButtonGroup>
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
      </div>

      <Card className="stat-card">
        <Card.Body className="chat-container">
          <div className="chat-messages">
            {messages.length === 0 && (
              <div className="text-center text-muted py-5">
                <div className="fs-1 mb-3">🤖</div>
                <p>Ask about risk, compliance, documents, or platform capabilities.</p>
                <Badge bg="info">Domain: {domainCode}</Badge>
                <p className="mt-2">
                  <small>Use 🎤 to speak your message or 🔊 to hear responses</small>
                </p>
              </div>
            )}
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`chat-bubble ${msg.role === 'USER' ? 'user' : 'assistant'}`}
              >
                <div className="message-content">{msg.content}</div>
                {msg.role === 'ASSISTANT' && (
                  <div className="mt-1">
                    <SpeechButton
                      mode="output"
                      textToSpeak={msg.content}
                      onSpeak={autoSpeak}
                      size="sm"
                    />
                  </div>
                )}
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
            <div className="flex-grow-1 d-flex gap-2">
              <Form.Control
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Type your message..."
                disabled={loading}
              />
              <SpeechButton
                mode="input"
                onTranscript={handleTranscript}
                disabled={loading}
              />
            </div>
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