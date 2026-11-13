import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Search,
  BookOpen,
  ArrowRight,
  ShieldCheck,
  Zap,
  Repeat,
  DollarSign,
  Users,
  Award,
  Sparkles,
  CheckCircle,
  GraduationCap
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

  // Get 4 recently listed/featured books
  const recentBooks = books.slice(0, 4);

  const categories = [
    { name: 'Computer Science', count: '14 Books', icon: '💻', query: 'Computer Science' },
    { name: 'Data Structures & Algorithms', count: '6 Books', icon: '🧩', query: 'Data Structures and Algorithms' },
    { name: 'Artificial Intelligence & ML', count: '5 Books', icon: '🤖', query: 'Artificial Intelligence' },
    { name: 'Engineering Mathematics', count: '4 Books', icon: '📐', query: 'Mathematics' },
    { name: 'Operating Systems & Networks', count: '5 Books', icon: '⚡', query: 'Operating Systems' },
    { name: 'First Year Engineering Sciences', count: '3 Books', icon: '🔬', query: 'Physics' }
  ];

  return (
    <div className="home-page">
      {/* 1. HERO SECTION */}
      <section
        style={{
          background: 'linear-gradient(135deg, #0f172a 0%, #172554 50%, #0f766e 100%)',
          color: '#ffffff',
          padding: '5rem 0 6rem 0',
          position: 'relative',
          overflow: 'hidden'
        }}
      >
        {/* Subtle decorative circles */}
        <div
          style={{
            position: 'absolute',
            top: '-10%',
            right: '-5%',
            width: '450px',
            height: '450px',
            borderRadius: '50%',
            background: 'radial-gradient(circle, rgba(13,148,136,0.25) 0%, transparent 70%)',
            pointerEvents: 'none'
          }}
        />
        <div
          style={{
            position: 'absolute',
            bottom: '-15%',
            left: '5%',
            width: '350px',
            height: '350px',
            borderRadius: '50%',
            background: 'radial-gradient(circle, rgba(99,102,241,0.2) 0%, transparent 70%)',
            pointerEvents: 'none'
          }}
        />

        <div className="container" style={{ position: 'relative', zIndex: 2 }}>
          <div
            className="hero-grid"
            style={{
              display: 'grid',
              gridTemplateColumns: '1.2fr 0.8fr',
              gap: '3.5rem',
              alignItems: 'center'
            }}
          >
            {/* Left Content */}
            <div>
              <div
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  padding: '0.4rem 1rem',
                  backgroundColor: 'rgba(255, 255, 255, 0.12)',
                  backdropFilter: 'blur(8px)',
                  borderRadius: 'var(--radius-full)',
                  fontSize: '0.85rem',
                  fontWeight: 600,
                  marginBottom: '1.5rem',
                  border: '1px solid rgba(255, 255, 255, 0.15)'
                }}
              >
                <Sparkles size={16} color="#2dd4bf" />
                <span>Verified Peer-to-Peer College Book Exchange</span>
              </div>

              <h1
                style={{
                  fontSize: '3.2rem',
                  lineHeight: 1.15,
                  fontWeight: 800,
                  color: '#ffffff',
                  marginBottom: '1.25rem'
                }}
              >
                Your Next Favorite Book Is Just a Swap Away.
              </h1>

              <p
                style={{
                  fontSize: '1.2rem',
                  color: '#cbd5e1',
                  marginBottom: '2rem',
                  lineHeight: 1.6,
                  maxWidth: '560px'
                }}
              >
                Discover affordable academic books, connect with fellow students, and give your old textbooks a meaningful new home. Save up to 75% on semester course materials!
              </p>

              {/* Search Box in Hero */}
              <form
                onSubmit={handleHeroSearch}
                className="hero-search-box"
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  backgroundColor: '#ffffff',
                  borderRadius: 'var(--radius-lg)',
                  padding: '0.4rem 0.5rem 0.4rem 1.25rem',
                  boxShadow: '0 10px 25px rgba(0, 0, 0, 0.25)',
                  maxWidth: '540px',
                  marginBottom: '1.75rem'
                }}
              >
                <Search size={20} color="#64748b" style={{ marginRight: '0.75rem', flexShrink: 0 }} />
                <input
                  type="text"
                  placeholder="Search by book title, author, or subject..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  style={{
                    border: 'none',
                    outline: 'none',
                    width: '100%',
                    fontSize: '0.95rem',
                    color: 'var(--text-main)'
                  }}
                />
                <button type="submit" className="btn btn-primary" style={{ flexShrink: 0 }}>
                  Search
                </button>
              </form>

              {/* Action Buttons */}
              <div className="hero-cta-group" style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                <Link to="/explore" className="btn btn-secondary btn-lg">
                  <BookOpen size={18} />
                  <span>Explore All Books</span>
                </Link>
                <Link
                  to="/add-book"
                  className="btn btn-outline btn-lg"
                  style={{ color: '#ffffff', borderColor: 'rgba(255,255,255,0.3)' }}
                >
                  <span>List a Book for Sale/Swap</span>
                  <ArrowRight size={18} />
                </Link>
              </div>
            </div>

            {/* Right Visual Card */}
            <div style={{ display: 'flex', justifyContent: 'center' }}>
              <div
                style={{
                  position: 'relative',
                  width: '100%',
                  maxWidth: '420px',
                  background: 'rgba(255, 255, 255, 0.08)',
                  backdropFilter: 'blur(16px)',
                  border: '1px solid rgba(255, 255, 255, 0.15)',
                  borderRadius: 'var(--radius-xl)',
                  padding: '2rem',
                  boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.35)'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <div style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: '#10B981' }} />
                    <span style={{ fontSize: '0.85rem', fontWeight: 600, color: '#94a3b8' }}>Live Campus Exchange</span>
                  </div>
                  <span style={{ fontSize: '0.8rem', padding: '0.2rem 0.6rem', backgroundColor: 'rgba(13, 148, 136, 0.3)', color: '#2dd4bf', borderRadius: 'var(--radius-full)' }}>
                    Active Now
                  </span>
                </div>

                <div
                  style={{
                    backgroundColor: '#ffffff',
                    color: 'var(--text-main)',
                    borderRadius: 'var(--radius-lg)',
                    padding: '1.25rem',
                    marginBottom: '1rem',
                    boxShadow: 'var(--shadow-md)'
                  }}
                >
                  <div style={{ display: 'flex', gap: '1rem' }}>
                    <img
                      src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=300&q=80"
                      alt="CLRS Algorithms"
                      style={{ width: '70px', height: '95px', objectFit: 'cover', borderRadius: 'var(--radius-sm)' }}
                    />
                    <div style={{ flex: 1 }}>
                      <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--secondary)' }}>EXCHANGE MATCHED</span>
                      <h4 style={{ fontSize: '0.95rem', margin: '0.2rem 0' }}>Introduction to Algorithms (CLRS)</h4>
                      <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Offered: Deep Learning (Goodfellow)</p>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '0.5rem', alignItems: 'center' }}>
                        <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>BITS Pilani ↔ DTU</span>
                        <span style={{ fontSize: '0.85rem', fontWeight: 800, color: 'var(--success)' }}>Saved ₹1,200</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '0.75rem 1rem',
                    backgroundColor: 'rgba(255, 255, 255, 0.05)',
                    borderRadius: 'var(--radius-md)',
                    fontSize: '0.85rem',
                    color: '#e2e8f0'
                  }}
                >
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Users size={16} color="#2dd4bf" />
                    <strong>1,400+ Students</strong> trading this week
                  </span>
                  <CheckCircle size={16} color="#10b981" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 2. PLATFORM STATISTICS */}
      <section style={{ backgroundColor: '#ffffff', borderBottom: '1px solid var(--border-light)', padding: '2.5rem 0' }}>
        <div className="container">
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: '2rem',
              textAlign: 'center'
            }}
          >
            <div>
              <div style={{ fontSize: '2.5rem', fontWeight: 800, color: 'var(--primary)', fontFamily: 'var(--font-heading)' }}>
                15,000+
              </div>
              <p style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                Academic Books Listed
              </p>
            </div>
            <div>
              <div style={{ fontSize: '2.5rem', fontWeight: 800, color: 'var(--secondary)', fontFamily: 'var(--font-heading)' }}>
                ₹18 Lakhs+
              </div>
              <p style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                Student Savings on Books
              </p>
            </div>
            <div>
              <div style={{ fontSize: '2.5rem', fontWeight: 800, color: 'var(--accent)', fontFamily: 'var(--font-heading)' }}>
                45+ Campuses
              </div>
              <p style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                Connected Student Networks
              </p>
            </div>
            <div>
              <div style={{ fontSize: '2.5rem', fontWeight: 800, color: '#10b981', fontFamily: 'var(--font-heading)' }}>
                99.4%
              </div>
              <p style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                Verified Exchange Satisfaction
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 3. RECENTLY LISTED BOOKS */}
      <section className="section-padding">
        <div className="container">
          <div style={{ display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between', marginBottom: '2.5rem', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <span className="section-badge">
                <BookOpen size={14} /> Fresh From Peers
              </span>
              <h2 className="section-title">Recently Listed Textbooks</h2>
              <p className="section-desc">Hand-picked course textbooks uploaded by fellow students today.</p>
            </div>
            <Link to="/explore" className="btn btn-outline" style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <span>View All 24+ Books</span>
              <ArrowRight size={16} />
            </Link>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(270px, 1fr))',
              gap: '1.75rem'
            }}
          >
            {recentBooks.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
        </div>
      </section>

      {/* 4. POPULAR CATEGORIES */}
      <section className="section-padding" style={{ backgroundColor: 'var(--bg-subtle)' }}>
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Syllabus Tailored</span>
            <h2 className="section-title">Popular Academic Subjects</h2>
            <p className="section-desc">
              Browse textbooks grouped according to semester courses, branch electives, and foundational engineering subjects.
            </p>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
              gap: '1.5rem'
            }}
          >
            {categories.map((cat, idx) => (
              <Link
                key={idx}
                to={`/explore?subject=${encodeURIComponent(cat.query)}`}
                className="card"
                style={{
                  padding: '1.5rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '1.25rem',
                  textDecoration: 'none',
                  color: 'var(--text-main)',
                  transition: 'all var(--transition-normal)'
                }}
              >
                <div
                  style={{
                    fontSize: '2rem',
                    width: '60px',
                    height: '60px',
                    backgroundColor: 'var(--primary-light)',
                    borderRadius: 'var(--radius-lg)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    flexShrink: 0
                  }}
                >
                  {cat.icon}
                </div>
                <div>
                  <h4 style={{ fontSize: '1.05rem', marginBottom: '0.25rem' }}>{cat.name}</h4>
                  <span style={{ fontSize: '0.85rem', color: 'var(--secondary)', fontWeight: 600 }}>
                    {cat.count} Available
                  </span>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* 5. HOW BOOKLOOP WORKS */}
      <section id="how-it-works" className="section-padding">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Simple 3-Step Process</span>
            <h2 className="section-title">How BookLoop Works</h2>
            <p className="section-desc">
              Peer-to-peer exchanging without middlemen, inflated bookstore commissions, or complicated logistics.
            </p>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
              gap: '2.5rem',
              position: 'relative'
            }}
          >
            {/* Step 1 */}
            <div
              className="card"
              style={{
                padding: '2.5rem 2rem',
                textAlign: 'center',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center'
              }}
            >
              <div
                style={{
                  width: '64px',
                  height: '64px',
                  borderRadius: '50%',
                  backgroundColor: 'var(--primary-light)',
                  color: 'var(--primary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '1.5rem',
                  fontWeight: 800,
                  marginBottom: '1.5rem'
                }}
              >
                1
              </div>
              <h3 style={{ fontSize: '1.3rem', marginBottom: '0.75rem' }}>List</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                Upload your used academic books in under 2 minutes. Specify book condition, semester, edition, and whether you want to sell or exchange.
              </p>
            </div>

            {/* Step 2 */}
            <div
              className="card"
              style={{
                padding: '2.5rem 2rem',
                textAlign: 'center',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center'
              }}
            >
              <div
                style={{
                  width: '64px',
                  height: '64px',
                  borderRadius: '50%',
                  backgroundColor: 'var(--secondary-light)',
                  color: 'var(--secondary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '1.5rem',
                  fontWeight: 800,
                  marginBottom: '1.5rem'
                }}
              >
                2
              </div>
              <h3 style={{ fontSize: '1.3rem', marginBottom: '0.75rem' }}>Discover</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                Search books by title, author, subject, or semester. Filter by price or find students willing to exchange for books you already own.
              </p>
            </div>

            {/* Step 3 */}
            <div
              className="card"
              style={{
                padding: '2.5rem 2rem',
                textAlign: 'center',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center'
              }}
            >
              <div
                style={{
                  width: '64px',
                  height: '64px',
                  borderRadius: '50%',
                  backgroundColor: 'var(--accent-light)',
                  color: 'var(--accent)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '1.5rem',
                  fontWeight: 800,
                  marginBottom: '1.5rem'
                }}
              >
                3
              </div>
              <h3 style={{ fontSize: '1.3rem', marginBottom: '0.75rem' }}>Exchange</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                Send a purchase or book swap request. Chat directly inside BookLoop, meet safely on campus or courier, and rate your peer.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 6. WHY CHOOSE BOOKLOOP? */}
      <section id="about-us" className="section-padding" style={{ backgroundColor: '#ffffff', borderTop: '1px solid var(--border-light)' }}>
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Student-Centric Benefits</span>
            <h2 className="section-title">Why Choose BookLoop?</h2>
            <p className="section-desc">
              Designed specifically by students, for students, solving real campus textbook hurdles.
            </p>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
              gap: '2rem'
            }}
          >
            <div style={{ display: 'flex', gap: '1.25rem' }}>
              <div
                style={{
                  width: '50px',
                  height: '50px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--primary-light)',
                  color: 'var(--primary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0
                }}
              >
                <DollarSign size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.15rem', marginBottom: '0.5rem' }}>Pocket-Friendly Academic Costs</h4>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  Stop paying full MRP for books you only need for 4 months. Buy gently used books at fraction of cost or swap for free.
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '1.25rem' }}>
              <div
                style={{
                  width: '50px',
                  height: '50px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--secondary-light)',
                  color: 'var(--secondary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0
                }}
              >
                <Repeat size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.15rem', marginBottom: '0.5rem' }}>1-to-1 Book Exchange Capability</h4>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  Have last semester's Math book? Trade it directly for this semester's Operating Systems textbook without spending a single rupee.
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '1.25rem' }}>
              <div
                style={{
                  width: '50px',
                  height: '50px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--accent-light)',
                  color: 'var(--accent)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0
                }}
              >
                <ShieldCheck size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.15rem', marginBottom: '0.5rem' }}>Verified College Profiles & Ratings</h4>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  Interact with real students from recognized universities. Inspect star ratings, reviews, and transaction track records before handoffs.
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '1.25rem' }}>
              <div
                style={{
                  width: '50px',
                  height: '50px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: '#fef3c7',
                  color: '#b45309',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0
                }}
              >
                <GraduationCap size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.15rem', marginBottom: '0.5rem' }}>Branch & Semester-Specific Filters</h4>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  No more sifting through irrelevant fiction or generic novels. Everything is strictly organized by academic syllabus and university curricula.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 7. STUDENT TESTIMONIALS */}
      <section className="section-padding" style={{ backgroundColor: 'var(--bg-subtle)' }}>
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Real Peer Stories</span>
            <h2 className="section-title">Loved by Students Nationwide</h2>
            <p className="section-desc">
              Hear from engineering and college undergrads who traded their books on BookLoop.
            </p>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
              gap: '2rem'
            }}
          >
            <div className="card" style={{ padding: '2rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.25rem' }}>
                <img
                  src="https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80"
                  alt="Priya Patel"
                  style={{ width: '50px', height: '50px', borderRadius: '50%', objectFit: 'cover' }}
                />
                <div>
                  <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem' }}>Priya Patel</h4>
                  <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>BITS Pilani • CSE 4th Sem</p>
                </div>
              </div>
              <p style={{ fontStyle: 'italic', color: 'var(--text-main)', fontSize: '0.95rem', lineHeight: 1.6 }}>
                "I swapped my 2nd-year C++ books for Cormen's CLRS Algorithms book. The exchange happened right at the campus cafeteria! Saved over ₹1,500 in one afternoon."
              </p>
            </div>

            <div className="card" style={{ padding: '2rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.25rem' }}>
                <img
                  src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80"
                  alt="Rohan Verma"
                  style={{ width: '50px', height: '50px', borderRadius: '50%', objectFit: 'cover' }}
                />
                <div>
                  <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem' }}>Rohan Verma</h4>
                  <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>IIT Bombay • EE 6th Sem</p>
                </div>
              </div>
              <p style={{ fontStyle: 'italic', color: 'var(--text-main)', fontSize: '0.95rem', lineHeight: 1.6 }}>
                "Listing my semester books took less than 2 minutes. The built-in request tracker and campus messaging make communication effortless and safe."
              </p>
            </div>

            <div className="card" style={{ padding: '2rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.25rem' }}>
                <img
                  src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80"
                  alt="Ananya Iyer"
                  style={{ width: '50px', height: '50px', borderRadius: '50%', objectFit: 'cover' }}
                />
                <div>
                  <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem' }}>Ananya Iyer</h4>
                  <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>VIT Vellore • AI/ML 3rd Sem</p>
                </div>
              </div>
              <p style={{ fontStyle: 'italic', color: 'var(--text-main)', fontSize: '0.95rem', lineHeight: 1.6 }}>
                "The ability to filter directly by subject and semester is a lifesaver when exam season approaches. BookLoop is an essential app for every university student!"
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 8. CALL TO ACTION SECTION */}
      <section
        style={{
          background: 'linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%)',
          color: '#ffffff',
          padding: '5rem 0',
          textAlign: 'center'
        }}
      >
        <div className="container" style={{ maxWidth: '750px' }}>
          <h2 style={{ fontSize: '2.5rem', color: '#ffffff', marginBottom: '1rem' }}>
            Ready to Clear Your Bookshelf & Save Money?
          </h2>
          <p style={{ fontSize: '1.15rem', color: '#cbd5e1', marginBottom: '2.5rem', lineHeight: 1.6 }}>
            Join thousands of college students exchanging, buying, and selling academic textbooks today on BookLoop.
          </p>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
            <Link to="/register" className="btn btn-secondary btn-lg">
              Create Free Student Account
            </Link>
            <Link to="/explore" className="btn btn-outline btn-lg" style={{ color: '#ffffff', borderColor: '#ffffff' }}>
              Browse Available Books
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

