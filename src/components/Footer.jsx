import React from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, Github, Twitter, Linkedin, Heart } from 'lucide-react';

export const Footer = () => {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-grid">
          {/* Brand Info */}
          <div className="footer-brand">
            <Link to="/" className="footer-logo">
              <div className="nav-logo-icon">
                <BookOpen size={20} strokeWidth={2.5} />
              </div>
              <span>BookLoop</span>
            </Link>
            <p className="footer-tagline">
              "Read More. Spend Less. Exchange Smarter."
            </p>
            <p style={{ color: '#94a3b8', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
              The dedicated student-to-student peer marketplace helping college students share academic books, cut education costs, and promote eco-friendly book recycling across campuses.
            </p>
            <div className="footer-socials">
              <a href="#" className="footer-social-btn" aria-label="Github">
                <Github size={18} />
              </a>
              <a href="#" className="footer-social-btn" aria-label="Twitter">
                <Twitter size={18} />
              </a>
              <a href="#" className="footer-social-btn" aria-label="LinkedIn">
                <Linkedin size={18} />
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="footer-col-title">Quick Navigation</h4>
            <ul className="footer-links">
              <li><Link to="/" className="footer-link">Home</Link></li>
              <li><Link to="/explore" className="footer-link">Browse Marketplace</Link></li>
              <li><Link to="/add-book" className="footer-link">List Academic Book</Link></li>
              <li><Link to="/dashboard" className="footer-link">Student Dashboard</Link></li>
              <li><Link to="/wishlist" className="footer-link">Saved Wishlist</Link></li>
            </ul>
          </div>

          {/* Top Subjects */}
          <div>
            <h4 className="footer-col-title">Popular Subjects</h4>
            <ul className="footer-links">
              <li><Link to="/explore?subject=Data+Structures+and+Algorithms" className="footer-link">Data Structures & Algo</Link></li>
              <li><Link to="/explore?subject=Database+Management+Systems" className="footer-link">Database Systems</Link></li>
              <li><Link to="/explore?subject=Operating+Systems" className="footer-link">Operating Systems</Link></li>
              <li><Link to="/explore?subject=Artificial+Intelligence" className="footer-link">Artificial Intelligence</Link></li>
              <li><Link to="/explore?subject=Mathematics" className="footer-link">Engineering Mathematics</Link></li>
            </ul>
          </div>

          {/* College Community */}
          <div>
            <h4 className="footer-col-title">Student Support</h4>
            <ul className="footer-links">
              <li><a href="#how-it-works" className="footer-link">How Peer Exchange Works</a></li>
              <li><a href="#" className="footer-link">Campus Safety Guidelines</a></li>
              <li><a href="#" className="footer-link">Book Condition Standards</a></li>
              <li><a href="#" className="footer-link">Community FAQs</a></li>
              <li><a href="#" className="footer-link">Report an Issue</a></li>
            </ul>
          </div>
        </div>

        {/* Footer Bottom */}
        <div className="footer-bottom">
          <p>© {new Date().getFullYear()} BookLoop Platform. Engineered for College Academic Excellence.</p>
          <p style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            Built with <Heart size={14} color="#EF4444" fill="#EF4444" /> for students across India.
          </p>
        </div>
      </div>
    </footer>
  );
};

