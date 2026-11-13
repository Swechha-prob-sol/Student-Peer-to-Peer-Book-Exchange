import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Search,
  BookOpen,
  ArrowRight,
  ShieldCheck,
  Repeat,
  PlusCircle,
  MapPin,
  Clock,
  CheckCircle,
  ArrowUpRight
} from 'lucide-react';
import { useBooks } from '../context/BookContext';
import { BookCard } from '../components/BookCard';

export const Home = () => {
  const { books } = useBooks();
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  const handleHeroSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/explore?search=${encodeURIComponent(searchQuery.trim())}`);
    } else {
      navigate('/explore');
    }
  };

  // Quick subject shortcuts that students actually look for
  const quickTags = [
    { label: 'Data Structures & Algorithms', query: 'Data Structures and Algorithms' },
    { label: 'Operating Systems', query: 'Operating Systems' },
    { label: 'Database Systems', query: 'Database Management Systems' },
    { label: 'AI & Machine Learning', query: 'Machine Learning' },
    { label: 'Engineering Math', query: 'Mathematics' },
    { label: 'Discrete Math', query: 'Discrete Mathematics' }
  ];

  const recentBooks = books.slice(0, 8);

  const liveActivity = [
    { text: 'Ananya requested swap for CLRS Algorithms', college: 'VIT Vellore', time: '12m ago' },
    { text: 'Rohan listed Korth Database Systems (₹490)', college: 'IIT Bombay', time: '34m ago' },
    { text: 'Priya completed handoff for Java Complete Ref', college: 'BITS Pilani', time: '1h ago' }
  ];

  return (
    <div className="home-page">
      {/* 1. CAMPUS ANNOUNCEMENT STRIP */}
      <div
        style={{
          backgroundColor: '#0f172a',
          color: '#e2e8f0',
          padding: '0.45rem 1rem',
          fontSize: '0.78rem',
          textAlign: 'center',
          borderBottom: '1px solid #1e293b'
        }}
      >
        <div className="container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
          <span style={{ backgroundColor: '#15803d', color: '#ffffff', padding: '0.1rem 0.4rem', borderRadius: '3px', fontWeight: 700, fontSize: '0.7rem' }}>
            CAMPUS P2P
          </span>
          <span>Semester textbook exchange open across DTU, BITS Pilani, IIT Bombay, VIT & NIT campuses.</span>
          <Link to="/explore" style={{ color: '#38bdf8', fontWeight: 600, textDecoration: 'underline', marginLeft: '0.25rem' }}>
            Find your course books →
          </Link>
        </div>
      </div>

      {/* 2. COMPACT, HUMAN-DESIGNED HERO SECTION */}
      <section
        style={{
          backgroundColor: '#ffffff',
          borderBottom: '1px solid var(--border-light)',
          padding: '2.5rem 0 2rem 0'
        }}
      >
        <div className="container">
          <div style={{ maxWidth: '820px' }}>
            <h1
              style={{
                fontSize: '2.4rem',
                lineHeight: 1.18,
                fontWeight: 800,
                color: 'var(--text-main)',
                marginBottom: '0.75rem',
                letterSpacing: '-0.025em'
              }}
            >
              Buy, sell or swap used college textbooks directly with students on campus.
            </h1>

            <p
              style={{
                fontSize: '1.05rem',
                color: 'var(--text-muted)',
                lineHeight: 1.5,
                marginBottom: '1.5rem',
                maxWidth: '680px'
              }}
            >
              Don't pay bookstore MRP for textbooks you only need for one semester. Connect directly with seniors and batchmates, arrange a quick library or canteen handoff, and trade books for cash or 1-to-1 swaps.
            </p>

            {/* Direct Search Bar */}
            <form
              onSubmit={handleHeroSearch}
              style={{
                display: 'flex',
                alignItems: 'center',
                backgroundColor: '#ffffff',
                border: '1.5px solid var(--border-light)',
                borderRadius: 'var(--radius-md)',
                padding: '0.25rem 0.35rem 0.25rem 0.85rem',
                boxShadow: 'var(--shadow-xs)',
                maxWidth: '640px',
                marginBottom: '1rem'
              }}
            >
              <Search size={18} color="var(--text-muted)" style={{ marginRight: '0.6rem', flexShrink: 0 }} />
              <input
                type="text"
                placeholder="Search by book title, author, subject, or professor syllabus..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                style={{
                  border: 'none',
                  outline: 'none',
                  width: '100%',
                  fontSize: '0.92rem',
                  color: 'var(--text-main)'
                }}
              />
              <button type="submit" className="btn btn-primary" style={{ padding: '0.5rem 1.1rem' }}>
                Search
              </button>
            </form>

            {/* Quick Subject Filter Pills */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', flexWrap: 'wrap', marginBottom: '1.5rem' }}>
              <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)', fontWeight: 600 }}>
                Popular courses:
              </span>
              {quickTags.map((tag, i) => (
                <Link
                  key={i}
                  to={`/explore?subject=${encodeURIComponent(tag.query)}`}
                  style={{
                    fontSize: '0.76rem',
                    padding: '0.2rem 0.55rem',
                    backgroundColor: 'var(--bg-subtle)',
                    color: 'var(--text-main)',
                    borderRadius: 'var(--radius-sm)',
                    border: '1px solid var(--border-light)',
                    textDecoration: 'none',
                    fontWeight: 500
                  }}
                >
                  {tag.label}
                </Link>
              ))}
            </div>

            {/* Actions */}
            <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
              <Link to="/add-book" className="btn btn-secondary">
                <PlusCircle size={15} />
                <span>List a Book in 2 Minutes</span>
              </Link>
              <Link to="/explore" className="btn btn-outline">
                <BookOpen size={15} />
                <span>Browse All 24+ Listings</span>
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* 3. LIVE CAMPUS ACTIVITY TICKER */}
      <section style={{ backgroundColor: 'var(--bg-subtle)', borderBottom: '1px solid var(--border-light)', padding: '0.75rem 0' }}>
        <div className="container">
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', flexWrap: 'wrap' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Recent campus activity:
            </span>
            <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', flex: 1 }}>
              {liveActivity.map((item, idx) => (
                <div key={idx} style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.8rem', color: 'var(--text-main)' }}>
                  <span style={{ width: '6px', height: '6px', borderRadius: '50%', backgroundColor: '#10b981' }} />
                  <span>{item.text}</span>
                  <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>• {item.college} ({item.time})</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* 4. RECENTLY LISTED BOOKS - DIRECTLY VISIBLE ON HOMEPAGE */}
      <section className="section-padding">
        <div className="container">
          <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '0.5rem' }}>
            <div>
              <h2 style={{ fontSize: '1.45rem', marginBottom: '0.2rem' }}>Recently Listed Textbooks</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                Handed down by seniors and peers this semester. All copies checked for condition and missing pages.
              </p>
            </div>
            <Link to="/explore" style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--accent)', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
              <span>View all available books</span>
              <ArrowRight size={14} />
            </Link>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(235px, 1fr))',
              gap: '1.25rem'
            }}
          >
            {recentBooks.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>

          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <Link to="/explore" className="btn btn-outline" style={{ minWidth: '180px' }}>
              See More Course Textbooks →
            </Link>
          </div>
        </div>
      </section>

      {/* 5. HOW IT WORKS (Compact 3-Step Walkthrough) */}
      <section id="how-it-works" className="section-padding" style={{ backgroundColor: '#ffffff', borderTop: '1px solid var(--border-light)', borderBottom: '1px solid var(--border-light)' }}>
        <div className="container">
          <div className="section-header" style={{ marginBottom: '1.5rem' }}>
            <h2 style={{ fontSize: '1.45rem', marginBottom: '0.25rem' }}>How Peer Exchange Works</h2>
            <p className="section-desc">Straightforward campus swaps with zero platform fees or middlemen.</p>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))',
              gap: '1.25rem'
            }}
          >
            <div className="card" style={{ padding: '1.5rem' }}>
              <div style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--secondary)', marginBottom: '0.4rem', textTransform: 'uppercase' }}>
                Step 01
              </div>
              <h3 style={{ fontSize: '1.1rem', marginBottom: '0.4rem' }}>List Old Semester Books</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: 1.5 }}>
                Snap a cover photo, specify edition, condition, and if you want cash (₹) or a swap for next semester's textbooks.
              </p>
            </div>

            <div className="card" style={{ padding: '1.5rem' }}>
              <div style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--secondary)', marginBottom: '0.4rem', textTransform: 'uppercase' }}>
                Step 02
              </div>
              <h3 style={{ fontSize: '1.1rem', marginBottom: '0.4rem' }}>Connect & Agree on Terms</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: 1.5 }}>
                Send a purchase or book swap request. Chat inside BookLoop to agree on the handoff location (library, canteen, hostel).
              </p>
            </div>

            <div className="card" style={{ padding: '1.5rem' }}>
              <div style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--secondary)', marginBottom: '0.4rem', textTransform: 'uppercase' }}>
                Step 03
              </div>
              <h3 style={{ fontSize: '1.1rem', marginBottom: '0.4rem' }}>Physical Handoff & Rating</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: 1.5 }}>
                Meet, check the book condition in person, complete the swap or pay via UPI/cash, and leave a quick peer review.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 6. CAMPUS SAFETY & STUDENT TIPS */}
      <section className="section-padding">
        <div className="container">
          <div
            className="card"
            style={{
              padding: '1.75rem 2rem',
              backgroundColor: 'var(--bg-subtle)',
              border: '1px solid var(--border-light)'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.75rem' }}>
              <ShieldCheck size={20} color="var(--secondary)" />
              <h3 style={{ fontSize: '1.1rem' }}>Campus Handoff Guidelines</h3>
            </div>
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
                gap: '1.5rem',
                fontSize: '0.85rem',
                color: 'var(--text-muted)'
              }}
            >
              <div>
                <strong style={{ color: 'var(--text-main)', display: 'block', marginBottom: '0.2rem' }}>
                  • Verify Before Paying
                </strong>
                Always flick through the pages during handoff to ensure syllabus chapters, diagrams, and formulas are intact.
              </div>
              <div>
                <strong style={{ color: 'var(--text-main)', display: 'block', marginBottom: '0.2rem' }}>
                  • Meet at Campus Hotspots
                </strong>
                Plan meetups at the Central Library, SAC, department canteen, or hostel common rooms for convenience and safety.
              </div>
              <div>
                <strong style={{ color: 'var(--text-main)', display: 'block', marginBottom: '0.2rem' }}>
                  • Instant UPI or Direct Trade
                </strong>
                Settle up via GPay/PhonePe or swap physical copies book-for-book without platform transaction fees.
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
