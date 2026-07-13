import React from 'react';
import { Button, Tooltip, Badge } from 'react-bootstrap';
import { useSpeechRecognition, useSpeechSynthesis } from '../hooks/useSpeech';

function SpeechButton({ 
  onTranscript, 
  onSpeak, 
  textToSpeak, 
  mode = 'input', 
  disabled = false,
  size = 'sm'
}) {
  const speechRecognition = useSpeechRecognition();
  const speechSynthesis = useSpeechSynthesis();

  // Handle speech-to-text for input mode
  const handleMicClick = async () => {
    if (mode === 'input') {
      if (speechRecognition.isListening) {
        speechRecognition.stopListening();
      } else {
        speechRecognition.startListening();
      }
    }
  };

  // Handle text-to-speech for output mode
  const handleSpeakerClick = () => {
    if (mode === 'output' && textToSpeak) {
      if (speechSynthesis.isSpeaking) {
        speechSynthesis.stop();
      } else {
        speechSynthesis.speak(textToSpeak, {
          rate: 1.0,
          pitch: 1.0,
          volume: 1.0,
        });
      }
    }
  };

  // Send transcript to parent when recognition ends
  React.useEffect(() => {
    if (mode === 'input' && speechRecognition.transcript && !speechRecognition.isListening) {
      onTranscript(speechRecognition.transcript);
      speechRecognition.resetTranscript();
    }
  }, [speechRecognition.transcript, speechRecognition.isListening, mode, onTranscript, speechRecognition]);

  // Auto-speak when textToSpeak changes (for output mode)
  React.useEffect(() => {
    if (mode === 'output' && textToSpeak && onSpeak === true) {
      speechSynthesis.speak(textToSpeak, {
        rate: 1.0,
        pitch: 1.0,
        volume: 1.0,
      });
    }
  }, [textToSpeak, mode, speechSynthesis, onSpeak]);

  if (mode === 'input') {
    return (
      <Tooltip 
        title={speechRecognition.error || (speechRecognition.isListening ? 'Stop listening' : 'Start voice input')}
      >
        <Button
          variant={speechRecognition.isListening ? 'danger' : 'outline-primary'}
          size={size}
          onClick={handleMicClick}
          disabled={disabled || !speechRecognition.isSupported}
          className="speech-button"
        >
          {speechRecognition.isListening ? (
            <>
              <span className="pulse">🎤</span> Listening...
            </>
          ) : (
            <>🎤</>
          )}
        </Button>
      </Tooltip>
    );
  }

  if (mode === 'output') {
    return (
      <Tooltip 
        title={speechSynthesis.error || (speechSynthesis.isSpeaking ? 'Stop speaking' : 'Read aloud')}
      >
        <Button
          variant={speechSynthesis.isSpeaking ? 'warning' : 'outline-secondary'}
          size={size}
          onClick={handleSpeakerClick}
          disabled={disabled || !textToSpeak || !speechSynthesis.isSupported}
          className="speech-button"
        >
          {speechSynthesis.isSpeaking ? '🔇' : '🔊'}
        </Button>
      </Tooltip>
    );
  }

  return null;
}

export default SpeechButton;