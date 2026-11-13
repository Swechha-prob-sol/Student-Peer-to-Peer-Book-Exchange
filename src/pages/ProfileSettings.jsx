import React, { useState } from 'react';
import {
  User,
  Mail,
  School,
  BookMarked,
  Phone,
  Camera,
  Save,
  CheckCircle,
  AlertCircle
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useBooks } from '../context/BookContext';
import { Sidebar } from '../components/Sidebar';
import { validateEmail, validateRequired } from '../utils/validation';

export const ProfileSettings = () => {
  const { currentUser, updateProfile } = useAuth();
  const { showToast } = useBooks();

  const [formData, setFormData] = useState({
    name: currentUser?.name || '',
    email: currentUser?.email || '',
    college: currentUser?.college || '',
    department: currentUser?.department || 'Computer Science and Engineering',
    semester: currentUser?.semester || '1',
    phone: currentUser?.phone || '',
    bio: currentUser?.bio || '',
    avatar: currentUser?.avatar || ''
  });

  const [avatarPreview, setAvatarPreview] = useState(currentUser?.avatar || '');
  const [errors, setErrors] = useState({});

  if (!currentUser) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  const handleAvatarFile = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setAvatarPreview(reader.result);
        setFormData((prev) => ({ ...prev, avatar: reader.result }));
      };
      reader.readAsDataURL(file);
    }
  };

  const presetAvatars = [
    'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80',
    'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80',
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80',
    'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80',
    'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80'
  ];

  const handleSubmit = (e) => {
    e.preventDefault();

    const errs = {};
    const nameErr = validateRequired(formData.name, 'Full Name');
    if (nameErr) errs.name = nameErr;

    const emailErr = validateEmail(formData.email);
    if (emailErr) errs.email = emailErr;

    const collegeErr = validateRequired(formData.college, 'College Name');
    if (collegeErr) errs.college = collegeErr;

    if (Object.keys(errs).length > 0) {
      setErrors(errs);
      return;
    }

    updateProfile(formData);
    showToast('Your profile details have been saved successfully!', 'success');
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div>
            <h1 style={{ fontSize: '1.85rem', marginBottom: '0.2rem' }}>Profile Settings</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Update your student credentials, verified university details, and contact information.
            </p>
          </div>
        </div>

        <div className="card" style={{ padding: '2.5rem', maxWidth: '750px' }}>
          <form onSubmit={handleSubmit}>
            {/* Avatar Section */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '2rem',
                paddingBottom: '2rem',
                borderBottom: '1px solid var(--border-light)',
                marginBottom: '2rem',
                flexWrap: 'wrap'
              }}
            >
              <div style={{ position: 'relative' }}>
                <img
                  src={avatarPreview}
                  alt={formData.name}
                  style={{
                    width: '100px',
                    height: '100px',
                    borderRadius: '50%',
                    objectFit: 'cover',
                    border: '3px solid var(--border-light)',
                    boxShadow: 'var(--shadow-sm)'
                  }}
                  onError={(e) => {
                    e.target.src = `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(formData.name)}`;
                  }}
                />
                <label
                  htmlFor="avatar-upload"
                  style={{
                    position: 'absolute',
                    bottom: '2px',
                    right: '2px',
                    backgroundColor: 'var(--primary)',
                    color: '#ffffff',
                    width: '32px',
                    height: '32px',
                    borderRadius: '50%',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'pointer',
                    boxShadow: '0 2px 6px rgba(0,0,0,0.2)'
                  }}
                  title="Upload profile photo"
                >
                  <Camera size={16} />
                </label>
                <input
                  id="avatar-upload"
                  type="file"
                  accept="image/*"
                  onChange={handleAvatarFile}
                  style={{ display: 'none' }}
                />
              </div>

              <div>
                <h4 style={{ fontSize: '1.1rem', marginBottom: '0.25rem' }}>Student Profile Avatar</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
                  Upload a clear portrait photo or pick from preset student avatars:
                </p>

                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  {presetAvatars.map((url, idx) => (
                    <img
                      key={idx}
                      src={url}
                      alt={`Preset ${idx}`}
                      onClick={() => {
                        setAvatarPreview(url);
                        setFormData((prev) => ({ ...prev, avatar: url }));
                      }}
                      style={{
                        width: '36px',
                        height: '36px',
                        borderRadius: '50%',
                        objectFit: 'cover',
                        cursor: 'pointer',
                        border: avatarPreview === url ? '2px solid var(--secondary)' : '1px solid var(--border-light)'
                      }}
                    />
                  ))}
                </div>
              </div>
            </div>

            {/* Full Name & Email */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
              <div className="form-group">
                <label className="form-label form-label-required">Full Name</label>
                <div style={{ position: 'relative' }}>
                  <User size={18} color="var(--text-muted)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
                  <input
                    type="text"
                    name="name"
                    className={`form-input ${errors.name ? 'error' : ''}`}
                    style={{ paddingLeft: '2.5rem' }}
                    value={formData.name}
                    onChange={handleChange}
                  />
                </div>
                {errors.name && <span className="form-error">{errors.name}</span>}
              </div>

              <div className="form-group">
                <label className="form-label form-label-required">College Email Address</label>
                <div style={{ position: 'relative' }}>
                  <Mail size={18} color="var(--text-muted)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
                  <input
                    type="email"
                    name="email"
                    className={`form-input ${errors.email ? 'error' : ''}`}
                    style={{ paddingLeft: '2.5rem' }}
                    value={formData.email}
                    onChange={handleChange}
                  />
                </div>
                {errors.email && <span className="form-error">{errors.email}</span>}
              </div>
            </div>

            {/* College & Department */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
              <div className="form-group">
                <label className="form-label form-label-required">College / University</label>
                <div style={{ position: 'relative' }}>
                  <School size={18} color="var(--text-muted)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
                  <input
                    type="text"
                    name="college"
                    className={`form-input ${errors.college ? 'error' : ''}`}
                    style={{ paddingLeft: '2.5rem' }}
                    value={formData.college}
                    onChange={handleChange}
                  />
                </div>
                {errors.college && <span className="form-error">{errors.college}</span>}
              </div>

              <div className="form-group">
                <label className="form-label form-label-required">Department / Branch</label>
                <select
                  name="department"
                  className="form-select"
                  value={formData.department}
                  onChange={handleChange}
                >
                  <option value="Computer Science and Engineering">Computer Science (CSE)</option>
                  <option value="Information Technology">Information Technology (IT)</option>
                  <option value="Data Science & AI">Data Science & AI</option>
                  <option value="Electronics & Communication">Electronics (ECE)</option>
                  <option value="Electrical Engineering">Electrical Engineering (EE)</option>
                  <option value="Mechanical Engineering">Mechanical Engineering</option>
                  <option value="Civil Engineering">Civil Engineering</option>
                </select>
              </div>
            </div>

            {/* Semester & Phone */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
              <div className="form-group">
                <label className="form-label form-label-required">Current Semester</label>
                <select
                  name="semester"
                  className="form-select"
                  value={formData.semester}
                  onChange={handleChange}
                >
                  {[1, 2, 3, 4, 5, 6, 7, 8].map((s) => (
                    <option key={s} value={String(s)}>
                      Semester {s}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <div style={{ position: 'relative' }}>
                  <Phone size={18} color="var(--text-muted)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
                  <input
                    type="text"
                    name="phone"
                    className="form-input"
                    style={{ paddingLeft: '2.5rem' }}
                    placeholder="+91 98765 43210"
                    value={formData.phone}
                    onChange={handleChange}
                  />
                </div>
              </div>
            </div>

            {/* Short Bio */}
            <div className="form-group">
              <label className="form-label">Student Bio</label>
              <textarea
                name="bio"
                rows="3"
                className="form-textarea"
                placeholder="A short introduction visible to other students browsing your profile..."
                value={formData.bio}
                onChange={handleChange}
              />
            </div>

            {/* Action Buttons */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end',
                gap: '1rem',
                borderTop: '1px solid var(--border-light)',
                paddingTop: '1.5rem',
                marginTop: '1.5rem'
              }}
            >
              <button
                type="button"
                onClick={() => {
                  setFormData({
                    name: currentUser.name,
                    email: currentUser.email,
                    college: currentUser.college,
                    department: currentUser.department,
                    semester: currentUser.semester,
                    phone: currentUser.phone,
                    bio: currentUser.bio,
                    avatar: currentUser.avatar
                  });
                  setAvatarPreview(currentUser.avatar);
                }}
                className="btn btn-outline"
              >
                Cancel Changes
              </button>

              <button type="submit" className="btn btn-primary btn-lg">
                <Save size={18} /> Save Profile Changes
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
};

