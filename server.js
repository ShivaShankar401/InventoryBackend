import express from 'express';
import session from 'express-session';
import cors from 'cors';
import bcrypt from 'bcryptjs';
import { v4 as uuidv4 } from 'uuid';

const app = express();
const PORT = 7090;

// --- Basic in-memory data store (replace with DB later) ---
const users = new Map();

function seedUsers() {
  const seed = [
    { name: 'Admin', email: 'admin@inventory.com', password: 'admin123', role: 'ADMIN' },
    { name: 'Staff', email: 'staff@inventory.com', password: 'staff123', role: 'STAFF' }
  ];
  seed.forEach((u) => {
    const id = uuidv4();
    const passwordHash = bcrypt.hashSync(u.password, 10);
    users.set(u.email.toLowerCase(), { id, name: u.name, email: u.email.toLowerCase(), passwordHash, role: u.role });
  });
}
seedUsers();

// --- Middleware ---
app.use(express.json());
app.use(
  cors({
    origin: 'http://localhost:3000',
    credentials: true
  })
);

app.use(
  session({
    name: 'ims.sid',
    secret: 'dev-secret-change-me',
    resave: false,
    saveUninitialized: false,
    cookie: {
      httpOnly: true,
      sameSite: 'lax', // works for localhost without https
      maxAge: 1000 * 60 * 60 * 8 // 8 hours
    }
  })
);

// --- Helpers ---
function sanitizeUser(u) {
  if (!u) return null;
  const { passwordHash, ...rest } = u;
  return rest;
}

// --- Routes ---
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});

app.post('/api/auth/register', async (req, res) => {
  try {
    const { name, email, password, role = 'STAFF' } = req.body || {};
    if (!name || !email || !password) {
      return res.status(400).json({ message: 'name, email and password are required' });
    }
    const key = String(email).toLowerCase();
    if (users.has(key)) {
      return res.status(409).json({ message: 'User already exists' });
    }
    const passwordHash = await bcrypt.hash(password, 10);
    const user = { id: uuidv4(), name, email: key, passwordHash, role };
    users.set(key, user);
    return res.status(201).json(sanitizeUser(user));
  } catch (err) {
    console.error('Register error:', err);
    return res.status(500).json({ message: 'Internal server error' });
  }
});

app.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body || {};
    if (!email || !password) {
      return res.status(400).json({ message: 'Email and password are required' });
    }
    const key = String(email).toLowerCase();
    const user = users.get(key);
    if (!user) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }
    const ok = await bcrypt.compare(password, user.passwordHash);
    if (!ok) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }
    req.session.user = sanitizeUser(user);
    return res.json(req.session.user);
  } catch (err) {
    console.error('Login error:', err);
    return res.status(500).json({ message: 'Internal server error' });
  }
});

app.post('/api/auth/logout', (req, res) => {
  try {
    req.session.destroy(() => {
      res.clearCookie('ims.sid');
      return res.json({ success: true });
    });
  } catch (err) {
    console.error('Logout error:', err);
    return res.status(500).json({ message: 'Internal server error' });
  }
});

app.get('/api/auth/me', (req, res) => {
  if (!req.session.user) {
    return res.status(401).json({ message: 'Not authenticated' });
  }
  return res.json(req.session.user);
});

app.listen(PORT, () => {
  console.log(`Inventory mock backend running on http://localhost:${PORT}`);
});
