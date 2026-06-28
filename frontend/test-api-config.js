// Test script to verify API configuration
const API_BASE = process.env.REACT_APP_API_URL || '';

console.log('API_BASE:', API_BASE);
console.log('Expected API URL:', `${API_BASE}/api/v1/chat`);

if (!API_BASE) {
  console.error('❌ ERROR: REACT_APP_API_URL is not set!');
  console.log('Please create a .env file with:');
  console.log('REACT_APP_API_URL=http://localhost:8080');
  process.exit(1);
} else {
  console.log('✅ API_BASE is configured correctly');
}