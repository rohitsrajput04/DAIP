# Speech Features Implementation

This document describes the Speech-to-Text and Text-to-Speech features implemented in the DAIP Platform.

## Overview

The DAIP Platform now supports voice interaction through:
- **Speech-to-Text (STT)**: Convert spoken words to text using the browser's Web Speech API
- **Text-to-Speech (TTS)**: Convert AI responses to spoken audio using the browser's Web Speech API

## Architecture

### Frontend Implementation

#### 1. Custom React Hooks (`src/hooks/useSpeech.js`)

**`useSpeechRecognition`** - Speech-to-Text Hook
- Uses the browser's native `SpeechRecognition` API
- Continuous listening mode with interim results
- Real-time transcript updates
- Error handling for unsupported browsers
- Methods: `startListening()`, `stopListening()`, `resetTranscript()`

**`useSpeechSynthesis`** - Text-to-Speech Hook
- Uses the browser's native `speechSynthesis` API
- Configurable speech parameters (rate, pitch, volume)
- Voice selection support
- Playback controls (play, stop, pause, resume)
- Methods: `speak()`, `stop()`, `pause()`, `resume()`, `getVoices()`

#### 2. Speech Button Component (`src/components/SpeechButton.js`)

A reusable component that provides:
- **Input Mode (🎤)**: Microphone button for voice input
  - Click to start/stop listening
  - Visual feedback with pulse animation when active
  - Automatic transcript transfer to parent component
  
- **Output Mode (🔊)**: Speaker button for text-to-speech
  - Click to play/stop AI response audio
  - Visual feedback showing speaking state
  - Auto-speak support via toggle

#### 3. Chat Integration (`src/pages/Chat.js`)

Enhanced chat interface with:
- **Voice Input**: Click the 🎤 button to speak your message
- **Auto-speak Toggle**: Enable automatic reading of AI responses
- **Manual Speak**: Click 🔊 on any AI message to hear it
- **Domain Selection**: Works with all domain modes (Core, Risk, Compliance, ESG, AML)

### Backend Implementation

#### Speech Controller (`backend/src/main/java/com/db/daip/controller/SpeechController.java`)

REST API endpoints for speech services:

**POST `/api/v1/speech/stt`** - Speech-to-Text
- Accepts audio file upload (multipart/form-data)
- Validates audio file
- Placeholder for integration with cloud services (Google, AWS, Azure, OpenAI)
- Returns transcribed text

**POST `/api/v1/speech/tts`** - Text-to-Speech
- Accepts text in request body
- Validates text length (max 5000 characters)
- Placeholder for integration with TTS services
- Returns status and text

**GET `/api/v1/speech/voices`** - Voice Listing
- Information about voice options
- Client-side voice management via Web Speech API

#### API Service (`src/services/api.js`)

Added `speechApi` object with methods:
- `speechToText(audioFile)` - Upload audio for transcription
- `textToSpeech(text)` - Request speech generation
- `getVoices()` - Get available voices

## Browser Compatibility

### Supported Browsers
- **Chrome/Edge**: Full support (recommended)
- **Safari**: Full support (requires HTTPS in production)
- **Firefox**: Speech Synthesis supported, Speech Recognition limited

### Requirements
- Modern browser with Web Speech API support
- Microphone permission for speech-to-text
- HTTPS connection in production (required for microphone access)

## Usage

### For Users

1. **Voice Input**:
   - Click the 🎤 microphone button next to the chat input
   - Grant microphone permission when prompted
   - Speak your message clearly
   - Click the button again to stop listening
   - Your speech will appear as text in the input field

2. **Text-to-Speech**:
   - Click the 🔊 speaker button on any AI response
   - The response will be read aloud
   - Click again to stop playback

3. **Auto-speak Mode**:
   - Click the "🔊 Auto-speak" toggle in the header
   - All AI responses will automatically be read aloud
   - Click individual 🔊 buttons to override for specific messages

### For Developers

#### Using the Speech Hooks

```javascript
import { useSpeechRecognition, useSpeechSynthesis } from '../hooks/useSpeech';

function MyComponent() {
  const speechRecognition = useSpeechRecognition();
  const speechSynthesis = useSpeechSynthesis();

  // Start listening
  const startListening = () => {
    speechRecognition.startListening();
  };

  // Get transcript
  useEffect(() => {
    if (speechRecognition.transcript) {
      console.log('Transcript:', speechRecognition.transcript);
    }
  }, [speechRecognition.transcript]);

  // Speak text
  const speakText = (text) => {
    speechSynthesis.speak(text, {
      rate: 1.0,
      pitch: 1.0,
      volume: 1.0
    });
  };

  return (
    <div>
      <button onClick={startListening}>🎤</button>
      <button onClick={() => speakText('Hello!')}>🔊</button>
    </div>
  );
}
```

#### Using the SpeechButton Component

```javascript
import SpeechButton from '../components/SpeechButton';

// For voice input
<SpeechButton
  mode="input"
  onTranscript={(text) => setInput(text)}
  disabled={loading}
/>

// For text-to-speech
<SpeechButton
  mode="output"
  textToSpeak="Text to speak"
  onSpeak={true}  // Auto-speak enabled
  size="sm"
/>
```

## Production Enhancements

### Backend Integration

To enable cloud-based speech services, update `SpeechController.java`:

**For Google Cloud Speech-to-Text:**
```java
// Add dependency: com.google.cloud:google-cloud-speech
SpeechClient client = SpeechClient.create();
// Configure recognition with audio file
```

**For AWS Transcribe:**
```java
// Add dependency: software.amazon.awssdk:transcribestreamingservice
TranscribeStreamingClient client = TranscribeStreamingClient.builder()...
```

**For Azure Speech Services:**
```java
// Add dependency: com.azure:azure-ai-speech
SpeechConfig config = SpeechConfig.fromSubscription(key, region);
```

**For OpenAI Whisper:**
```java
// Use OpenAI API endpoint
// POST https://api.openai.com/v1/audio/transcriptions
```

### Frontend Enhancements

1. **Language Selection**: Add dropdown for language selection
2. **Voice Selection**: Allow users to choose different TTS voices
3. **Speed Control**: Add sliders for speech rate and pitch
4. **Audio Visualization**: Show waveform during recording
5. **Transcript Editing**: Allow users to edit transcripts before sending
6. **History**: Save voice commands and transcripts

## Testing

### Manual Testing Checklist

- [ ] Microphone permission is requested when clicking 🎤
- [ ] Speech is transcribed accurately
- [ ] Transcript appears in input field
- [ ] Clicking 🎤 again stops listening
- [ ] Clicking 🔊 plays AI response
- [ ] Clicking 🔊 again stops playback
- [ ] Auto-speak toggle works correctly
- [ ] Error messages display for unsupported browsers
- [ ] Works in Chrome, Edge, and Safari
- [ ] Responsive on mobile devices

### Browser Console Testing

```javascript
// Test Speech Recognition
const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
recognition.continuous = true;
recognition.interimResults = true;
recognition.start();

// Test Speech Synthesis
const utterance = new SpeechSynthesisUtterance('Hello, world!');
window.speechSynthesis.speak(utterance);
```

## Troubleshooting

### Common Issues

1. **Microphone not working**:
   - Check browser permissions
   - Ensure HTTPS in production
   - Try Chrome or Edge for best support

2. **Speech not recognized**:
   - Speak clearly and at moderate pace
   - Reduce background noise
   - Check microphone quality

3. **Audio not playing**:
   - Check system volume
   - Ensure browser isn't muted
   - Try a different browser

4. **"Not supported" error**:
   - Update browser to latest version
   - Use Chrome, Edge, or Safari
   - Firefox has limited STT support

## Security Considerations

- All speech processing happens client-side (Web Speech API)
- No audio data is sent to servers unless backend STT/TTS is enabled
- Microphone access requires user permission
- HTTPS required for microphone access in production
- Backend endpoints are protected with JWT authentication

## Performance

- **Latency**: Near real-time for Web Speech API
- **Accuracy**: Depends on browser implementation and ambient noise
- **Resource Usage**: Minimal - uses browser's built-in capabilities
- **Offline Support**: Speech Synthesis works offline; Speech Recognition requires internet

## Future Enhancements

1. **Multi-language Support**: Add language selection for STT/TTS
2. **Custom Voice Training**: Allow users to train custom voices
3. **Audio Recording**: Save and replay voice messages
4. **Real-time Transcription**: Show live transcript during recording
5. **Command Recognition**: Special commands for navigation
6. **Accessibility**: Enhanced screen reader support
7. **Analytics**: Track speech feature usage
8. **Cloud Integration**: Optional cloud-based STT/TTS for better accuracy

## Dependencies

### Frontend
- No additional npm packages required
- Uses native browser APIs
- React hooks for state management

### Backend (Optional)
- Google Cloud Speech SDK
- AWS Transcribe SDK
- Azure Speech SDK
- OpenAI API client

## Support

For issues or questions:
1. Check browser compatibility
2. Review console logs for errors
3. Ensure microphone permissions are granted
4. Test with different browsers
5. Check network connectivity for cloud services

## License

Part of the DAIP Platform - DB AI Decision Intelligence Platform