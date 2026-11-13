import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Upload,
  BookOpen,
  Image,
  Sparkles,
  CheckCircle,
  ArrowLeft,
  Save,
  AlertCircle
} from 'lucide-react';
import { useBooks } from '../context/BookContext';
import { useAuth } from '../context/AuthContext';
import {
  SUBJECT_OPTIONS,
  SEMESTER_OPTIONS,
  CONDITION_OPTIONS,
  LISTING_TYPE_OPTIONS
} from '../data/mockBooks';
import { validateRequired, validatePrice } from '../utils/validation';

export const AddBook = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const editId = searchParams.get('edit');

  const { addBook, updateBook, getBookById } = useBooks();
  const { currentUser } = useAuth();

  const [formData, setFormData] = useState({
    title: '',
    author: '',
    isbn: '',
    subject: SUBJECT_OPTIONS[1],
    semester: '1',
    edition: '1st Edition',
    condition: 'Very Good',
    listingType: 'For Sale',
    price: '',
    preferredExchange: '',
    description: '',
    coverImage: ''
  });

  const [imagePreview, setImagePreview] = useState('');
  const [errors, setErrors] = useState({});
  const [isEditMode, setIsEditMode] = useState(false);

  // If in edit mode, populate form with existing book data
  useEffect(() => {
    if (editId) {
      const existing = getBookById(editId);
      if (existing) {
        setIsEditMode(true);
        setFormData({
          title: existing.title,
          author: existing.author,
          isbn: existing.isbn || '',
          subject: existing.subject,
          semester: String(existing.semester),
          edition: existing.edition,
          condition: existing.condition,
          listingType: existing.listingType,
          price: existing.price !== undefined ? String(existing.price) : '',
          preferredExchange: existing.preferredExchange || '',
          description: existing.description,
          coverImage: existing.coverImage || ''
        });
        setImagePreview(existing.coverImage || '');
      }
    }
  }, [editId, getBookById]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  // Image upload handling with instant FileReader preview
  const handleImageFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
        setFormData((prev) => ({ ...prev, coverImage: reader.result }));
      };
      reader.readAsDataURL(file);
    }
  };

  // Sample book covers helper for students who want a quick professional cover
  const presetCovers = [
    { label: 'Engineering/CS Navy', url: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80' },
    { label: 'Calculus/Science Green', url: 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?auto=format&fit=crop&w=600&q=80' },
    { label: 'Programming Amber', url: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=600&q=80' },
    { label: 'Modern Textbook Purple', url: 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=600&q=80' }
  ];

  const handleSelectPreset = (url) => {
    setImagePreview(url);
    setFormData((prev) => ({ ...prev, coverImage: url }));
  };

  const validateForm = () => {
    const errs = {};
    const titleErr = validateRequired(formData.title, 'Book Title');
    if (titleErr) errs.title = titleErr;

    const authorErr = validateRequired(formData.author, 'Author');
    if (authorErr) errs.author = authorErr;

    const descErr = validateRequired(formData.description, 'Description');
    if (descErr) errs.description = descErr;

    const priceErr = validatePrice(formData.price, formData.listingType);
    if (priceErr) errs.price = priceErr;

    if (formData.listingType === 'For Exchange' || formData.listingType === 'Both') {
      if (!formData.preferredExchange.trim()) {
        errs.preferredExchange = 'Please specify which book or subject you would like in return';
      }
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSave = (status = 'Active') => {
    if (!validateForm()) return;

    const payload = {
      ...formData,
      status
    };

    if (isEditMode) {
      updateBook(editId, payload);
    } else {
      addBook(payload);
    }

    navigate('/my-listings');
  };

  return (
    <div className="add-book-page page-wrapper" style={{ padding: '2.5rem 0 5rem 0' }}>
      <div className="container" style={{ maxWidth: '820px' }}>
        {/* Navigation Breadcrumb */}
        <div style={{ marginBottom: '1.5rem' }}>
          <button
            onClick={() => navigate(-1)}
            className="btn btn-ghost btn-sm"
            style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-muted)' }}
          >
            <ArrowLeft size={16} />
            Back
          </button>
        </div>

        <div className="card" style={{ padding: '2.5rem' }}>
          <div style={{ marginBottom: '2rem' }}>
            <span className="section-badge">
              <BookOpen size={14} /> {isEditMode ? 'Update Listing' : 'Marketplace Upload'}
            </span>
            <h1 style={{ fontSize: '2rem', marginBottom: '0.4rem' }}>
              {isEditMode ? 'Edit Book Listing' : 'List a Textbook for Sale or Swap'}
            </h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
              Fill in accurate details to help campus peers find and verify your textbook.
            </p>
          </div>

          <form onSubmit={(e) => { e.preventDefault(); handleSave('Active'); }}>
            {/* Title & Author */}
            <div className="form-group">
              <label className="form-label form-label-required">Book Title</label>
              <input
                type="text"
                name="title"
                className={`form-input ${errors.title ? 'error' : ''}`}
                placeholder="e.g. Introduction to Algorithms (CLRS)"
                value={formData.title}
                onChange={handleChange}
              />
              {errors.title && <span className="form-error">{errors.title}</span>}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '1.25rem' }}>
              <div className="form-group">
                <label className="form-label form-label-required">Author(s)</label>
                <input
                  type="text"
                  name="author"
                  className={`form-input ${errors.author ? 'error' : ''}`}
                  placeholder="e.g. Cormen, Leiserson, Rivest, Stein"
                  value={formData.author}
                  onChange={handleChange}
                />
                {errors.author && <span className="form-error">{errors.author}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">ISBN (Optional)</label>
                <input
                  type="text"
                  name="isbn"
                  className="form-input"
                  placeholder="e.g. 978-0262033848"
                  value={formData.isbn}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Subject & Semester */}
            <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '1.25rem' }}>
              <div className="form-group">
                <label className="form-label form-label-required">Academic Subject</label>
                <select
                  name="subject"
                  className="form-select"
                  value={formData.subject}
                  onChange={handleChange}
                >
                  {SUBJECT_OPTIONS.filter((s) => s !== 'All Subjects').map((s) => (
                    <option key={s} value={s}>
                      {s}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label form-label-required">Recommended Semester</label>
                <select
                  name="semester"
                  className="form-select"
                  value={formData.semester}
                  onChange={handleChange}
                >
                  {SEMESTER_OPTIONS.filter((s) => s !== 'All Semesters').map((s) => (
                    <option key={s} value={s}>
                      Semester {s}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Edition & Condition */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.25rem' }}>
              <div className="form-group">
                <label className="form-label form-label-required">Edition</label>
                <input
                  type="text"
                  name="edition"
                  className="form-input"
                  placeholder="e.g. 3rd Edition (MIT Press)"
                  value={formData.edition}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label form-label-required">Physical Condition</label>
                <select
                  name="condition"
                  className="form-select"
                  value={formData.condition}
                  onChange={handleChange}
                >
                  {CONDITION_OPTIONS.filter((c) => c !== 'All Conditions').map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Listing Type Selector */}
            <div className="form-group">
              <label className="form-label form-label-required">Listing Type</label>
              <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                {['For Sale', 'For Exchange', 'Both'].map((type) => (
                  <label
                    key={type}
                    style={{
                      flex: 1,
                      minWidth: '140px',
                      padding: '0.85rem 1rem',
                      borderRadius: 'var(--radius-md)',
                      border: formData.listingType === type ? '2px solid var(--secondary)' : '1px solid var(--border-light)',
                      backgroundColor: formData.listingType === type ? 'var(--secondary-light)' : '#ffffff',
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.5rem',
                      fontWeight: 600,
                      color: formData.listingType === type ? 'var(--secondary-hover)' : 'var(--text-main)',
                      transition: 'all 0.15s ease'
                    }}
                  >
                    <input
                      type="radio"
                      name="listingType"
                      value={type}
                      checked={formData.listingType === type}
                      onChange={handleChange}
                    />
                    {type}
                  </label>
                ))}
              </div>
            </div>

            {/* Conditional Price Field */}
            {formData.listingType !== 'For Exchange' && (
              <div className="form-group">
                <label className="form-label form-label-required">Price in Indian Rupees (₹)</label>
                <div style={{ position: 'relative' }}>
                  <span
                    style={{
                      position: 'absolute',
                      left: '1rem',
                      top: '50%',
                      transform: 'translateY(-50%)',
                      fontWeight: 700,
                      color: 'var(--text-muted)'
                    }}
                  >
                    ₹
                  </span>
                  <input
                    type="number"
                    name="price"
                    className={`form-input ${errors.price ? 'error' : ''}`}
                    style={{ paddingLeft: '2.5rem' }}
                    placeholder="e.g. 450"
                    value={formData.price}
                    onChange={handleChange}
                  />
                </div>
                {errors.price && <span className="form-error">{errors.price}</span>}
              </div>
            )}

            {/* Conditional Preferred Exchange Books Field */}
            {(formData.listingType === 'For Exchange' || formData.listingType === 'Both') && (
              <div className="form-group">
                <label className="form-label form-label-required">Preferred Book(s) for Swap</label>
                <input
                  type="text"
                  name="preferredExchange"
                  className={`form-input ${errors.preferredExchange ? 'error' : ''}`}
                  placeholder="e.g. Looking for Operating Systems by Silberschatz or Computer Networks by Tanenbaum"
                  value={formData.preferredExchange}
                  onChange={handleChange}
                />
                {errors.preferredExchange && <span className="form-error">{errors.preferredExchange}</span>}
              </div>
            )}

            {/* Description */}
            <div className="form-group">
              <label className="form-label form-label-required">Description & Notes</label>
              <textarea
                name="description"
                rows="4"
                className={`form-textarea ${errors.description ? 'error' : ''}`}
                placeholder="Mention any annotations, pencil markings, binding condition, or included lab CDs..."
                value={formData.description}
                onChange={handleChange}
              />
              {errors.description && <span className="form-error">{errors.description}</span>}
            </div>

            {/* Cover Image Upload & Preview */}
            <div className="form-group" style={{ marginBottom: '2.5rem' }}>
              <label className="form-label">Book Cover Image</label>
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: '160px 1fr',
                  gap: '1.5rem',
                  alignItems: 'start'
                }}
              >
                {/* Image Preview Box */}
                <div
                  style={{
                    width: '160px',
                    height: '210px',
                    borderRadius: 'var(--radius-md)',
                    border: '2px dashed var(--border-light)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    backgroundColor: 'var(--bg-subtle)',
                    overflow: 'hidden',
                    position: 'relative'
                  }}
                >
                  {imagePreview ? (
                    <img
                      src={imagePreview}
                      alt="Preview"
                      style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                    />
                  ) : (
                    <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '0.5rem' }}>
                      <Image size={32} style={{ margin: '0 auto 0.5rem auto', opacity: 0.5 }} />
                      <span style={{ fontSize: '0.75rem' }}>Cover Preview</span>
                    </div>
                  )}
                </div>

                {/* Upload or Choose Presets */}
                <div>
                  <div style={{ marginBottom: '1rem' }}>
                    <label
                      htmlFor="cover-file"
                      className="btn btn-outline btn-sm"
                      style={{ cursor: 'pointer', display: 'inline-flex' }}
                    >
                      <Upload size={16} />
                      <span>Upload Photo from Device</span>
                    </label>
                    <input
                      id="cover-file"
                      type="file"
                      accept="image/*"
                      onChange={handleImageFileChange}
                      style={{ display: 'none' }}
                    />
                  </div>

                  <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
                    Or select a professional cover placeholder for quick listing:
                  </p>

                  <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                    {presetCovers.map((preset, idx) => (
                      <button
                        key={idx}
                        type="button"
                        onClick={() => handleSelectPreset(preset.url)}
                        className="btn btn-ghost btn-sm"
                        style={{
                          fontSize: '0.75rem',
                          border: '1px solid var(--border-light)',
                          backgroundColor: imagePreview === preset.url ? 'var(--primary-light)' : '#ffffff'
                        }}
                      >
                        {preset.label}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Action Buttons: Publish, Draft, Cancel */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end',
                gap: '1rem',
                borderTop: '1px solid var(--border-light)',
                paddingTop: '1.5rem',
                flexWrap: 'wrap'
              }}
            >
              <button
                type="button"
                onClick={() => navigate(-1)}
                className="btn btn-outline"
              >
                Cancel
              </button>

              <button
                type="button"
                onClick={() => handleSave('Draft')}
                className="btn btn-ghost"
                style={{ border: '1px dashed var(--border-light)' }}
              >
                <Save size={16} />
                <span>Save as Draft</span>
              </button>

              <button
                type="submit"
                className="btn btn-primary btn-lg"
              >
                <CheckCircle size={18} />
                <span>{isEditMode ? 'Update Listing' : 'Publish Listing'}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

