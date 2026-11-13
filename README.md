# BookLoop – Student Peer-to-Peer Book Exchange Platform

> **"Read More. Spend Less. Exchange Smarter."**

BookLoop is a student-focused, peer-to-peer marketplace where college students can buy, sell, and exchange used academic textbooks with peers on campus. Built with modern React.js, Vite, and custom CSS, it provides a startup-grade UI/UX designed for academic project demonstration and future Java Spring Boot backend integration.

---

## 🌟 Key Features Built

### 1. 🏠 Landing Page (`/`)
- **Hero Section**: Tagline, dynamic search box, quick actions ("Explore All Books", "List a Book for Sale/Swap"), and live exchange teaser card.
- **Platform Metrics**: 15,000+ textbooks listed, ₹18 Lakhs+ saved, 45+ campus networks, 99.4% satisfaction score.
- **Recently Listed Carousel/Grid**: Direct previews of books listed by fellow students.
- **Popular Academic Categories**: Grouped by semester subjects (DSA, AI/ML, OS & Networks, Math, etc.).
- **How BookLoop Works**: 3-step walkthrough (List → Discover → Exchange).
- **Why Choose BookLoop**: Value props focusing on affordability, verified college profiles, syllabus filtering, and direct swaps.
- **Student Testimonials**: Real peer experiences from students across Indian universities.
- **Comprehensive Footer**: Subject links, campus safety guides, and quick navigation.

### 2. 🔍 Explore Books Marketplace (`/explore`)
- **Responsive Card Grid**: Displays book covers with automated fallback for missing/broken images, titles, authors, editions, conditions, semester badges, prices in ₹, and seller ratings.
- **Multi-parameter Search & Filters**:
  - Live text search across title, author, subject keywords, and annotations.
  - Filter by Subject (14+ core engineering & science subjects).
  - Filter by Semester (Semesters 1 through 8).
  - Filter by Condition (Like New, Very Good, Good, Acceptable).
  - Filter by Listing Type (For Sale, For Exchange, Both).
  - Interactive Max Price Slider (₹100 to ₹1,200+).
- **Sorting Options**: Newest First, Oldest First, Price: Low to High, Price: High to Low, Highest Rated Sellers.
- **Active Filter Pills**: One-click dismissible tags for active criteria.
- **Load More Pagination**: Smooth client-side batching.
- **Empty State Recovery**: Friendly reset button when filters return 0 matches.

### 3. 📖 Book Details & Interaction (`/books/:id`)
- **Detailed Specifications**: Large cover, title, author, ISBN, condition, edition, semester, university notes, and description.
- **Direct Actions**:
  - **Send Purchase Request**: Modal to input meeting place, preferred date, and notes.
  - **Request Book Swap**: Interactive modal enabling students to choose one of their own listed books to propose as an exchange item.
  - **Wishlist Toggle**: One-click heart icon with live count in the navbar.
  - **Chat with Seller**: Instantly starts a message conversation with the seller.
- **Related Books Section**: Suggestions from the same branch or subject curriculum.

### 4. ➕ Add / List a Book (`/add-book`)
- **Listing Details**: Title, Author, ISBN, Academic Subject, Recommended Semester, Edition, Physical Condition.
- **Conditional Fields**:
  - Shows price input in ₹ when listing for Sale or Both.
  - Shows preferred exchange swap field when listing for Exchange or Both.
- **Cover Photo Upload & Preview**: Upload from device with instant FileReader preview, or pick from curated academic textbook cover presets.
- **Dual Mode**: Supports creating new listings as well as editing existing listings (`?edit=:id`).
- **Save as Draft or Publish**: Directly updates state and redirects to My Listings.

### 5. 📊 Student Dashboard (`/dashboard`)
- **Sidebar Navigation**: Overview, My Listings, My Requests, Exchange Tracking, Wishlist, Messages, My Reviews, Profile Settings, and Logout.
- **Statistics Cards**: Total Listed Books, Active Listings, Pending Requests, Completed Swaps.
- **Recent Inquiries Stream**: Shows incoming and outgoing book inquiries.
- **Quick Actions**: List a Book, Track Swaps, Open Messages.
- **Active Listings & Campus Feed**: Immediate overview of personal and peer books.

### 6. 📚 My Listings Management (`/my-listings`)
- Filter by status tabs: **All, Active, Pending, Sold, Exchanged, Draft**.
- Direct status update buttons (Mark as Sold, Mark as Exchanged, Reactivate).
- Edit listing mode (pre-populates existing data).
- Delete listing with confirmation modal.

### 7. 📬 Request Management (`/requests`)
- **Received Requests Tab**:
  - Shows requester name, college, avatar, requested book, request type, date, and offered exchange item.
  - Actions: **Accept**, **Reject**, **Chat**, **Details Modal**.
- **Sent Requests Tab**:
  - Shows requests sent to other student sellers.
  - Actions: **Cancel Request**, **Chat**, **Details Modal**.

### 8. 🔄 Exchange Tracking & Timeline (`/exchange-tracking`)
- **Visual 5-Stage Timeline**:
  1. `Request Sent`
  2. `Request Accepted`
  3. `Contact Established`
  4. `Exchange Completed`
  5. `Review Submitted`
- Visual status progress bar with active and completed states.
- Button to advance through stages as handoff progresses.
- Automatic prompt to leave a 5-star peer review upon stage 4/5 completion.

### 9. 💬 In-App Messaging (`/messages`)
- Split-panel interface (Left: search conversations, avatar, unread badges; Right: chat thread).
- Real simulated messaging with timestamps and immediate UI update.
- Auto-scroll to the newest message.
- Persisted to localStorage.

### 10. ❤️ Wishlist (`/wishlist`)
- Grid of saved books with prices, sellers, and quick view buttons.
- One-click remove from wishlist.
- Syncs with navbar badge indicator.

### 11. 👤 Seller Profile (`/seller/:id`)
- Student verified badge, university, branch, semester, and bio.
- Seller reputation score, books sold, and exchanges completed.
- Full catalog of books currently listed by this seller.
- Previous peer reviews and star ratings.

### 12. ⚙️ Profile Settings (`/profile`)
- Edit Name, College Email, College/University, Branch, Semester, Phone, and Bio.
- Avatar selection from presets or custom photo upload.
- Full validation with instant localStorage update.

### 13. ⭐ My Reviews & Ratings (`/my-reviews`)
- Two tabs: **Reviews Received** and **Reviews Submitted**.
- Overall seller score calculation.
- Modal to submit a 5-star rating with written feedback for any peer transaction.

### 14. 🔐 Mock Authentication (`/login`, `/register`)
- **Login**: Email, Password, Show/Hide password toggle, Remember Me, Forgot Password modal.
- **Quick Demo Account Fillers**: One-click buttons to sign in as Aarav (DTU), Priya (BITS), Rohan (IITB), or Vikram (NIT).
- **Register**: Validates full name, university email format, password matching, strength meter, department, and semester.
- Protected route handling for all student dashboard pages.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Framework** | React.js 18 (Functional Components, Hooks) |
| **Bundler & Dev Server** | Vite 5 |
| **Navigation** | React Router DOM v6 |
| **Icons** | Lucide React |
| **Styling** | Custom CSS3 (CSS Variables, Flexbox, CSS Grid, Responsive Media Queries) |
| **State Management** | React Context API (`AuthContext`, `BookContext`) |
| **Persistence** | Browser `localStorage` with automated mock seed defaults |

---

## 🎨 Color Palette & Design System

- **Primary**: Deep Navy Blue (`#172554`)
- **Secondary**: Teal (`#0D9488`)
- **Accent**: Soft Indigo (`#6366F1`)
- **Background**: Light Off-White (`#F8FAFC`)
- **Cards**: Pure White (`#FFFFFF`)
- **Text Main**: Dark Slate (`#1E293B`)
- **Text Muted**: Slate Grey (`#64748B`)
- **Borders**: Soft Grey (`#E2E8F0`)
- **Success / Warning / Error**: Emerald (`#10B981`) / Amber (`#F59E0B`) / Rose (`#EF4444`)

---

## 🚀 Getting Started

### Prerequisites
- Node.js (v18 or higher recommended)
- npm or pnpm

### Installation & Run

```bash
# 1. Install dependencies
npm install

# 2. Run local development server
npm run dev

# 3. Build for production
npm run build

# 4. Preview production build
npm run preview
```

The application will start at `http://localhost:3000` (or the port specified in terminal output).

---

## 📁 Project Directory Structure

```
BookLoop/
├── index.html                   # HTML5 Entrypoint with Google Fonts
├── package.json                 # Dependencies & Build Scripts
├── vite.config.js               # Vite Configuration (port 3000)
├── README.md                    # Project Documentation
└── src/
    ├── main.jsx                 # Application Bootstrap
    ├── App.jsx                  # Main App with Router & Providers
    ├── data/
    │   ├── mockBooks.js         # 24+ Realistic Academic Books (CLRS, OS, DBMS, AI, etc.)
    │   ├── mockUsers.js         # College Student Profiles from DTU, BITS, IIT, VIT, NIT
    │   ├── mockRequests.js      # Sample Received & Sent Swap/Purchase Proposals
    │   ├── mockMessages.js      # Mock Conversation Threads
    │   └── mockReviews.js       # Peer Reviews & Ratings
    ├── context/
    │   ├── AuthContext.jsx      # Authentication & User Management State
    │   └── BookContext.jsx      # Marketplace, Requests, Wishlist, Messaging State
    ├── utils/
    │   ├── localStorage.js      # Safe LocalStorage Wrapper & Seed Handler
    │   └── validation.js        # Form Validation Rules (Email, Password, Price, Required)
    ├── components/
    │   ├── Navbar.jsx           # Responsive Top Navigation with Badges & Mobile Drawer
    │   ├── Footer.jsx           # Academic Links & Platform Info
    │   ├── Sidebar.jsx          # Dashboard Navigation Sidebar with Counters
    │   ├── BookCard.jsx         # Card Component for Marketplace & Listings
    │   ├── BookCoverFallback.jsx# Smart SVG/Gradient Fallback for Book Covers
    │   ├── SearchBar.jsx        # Search Input with Reset Action
    │   ├── FilterPanel.jsx      # Subject, Semester, Condition & Price Filters
    │   ├── Modal.jsx            # Reusable Accessible Dialog Modal
    │   ├── Toast.jsx            # Feedback Toast Notification Popup
    │   ├── RatingStars.jsx      # Read-only or Interactive 5-Star Component
    │   ├── StatusBadge.jsx      # Badges for Active, Sold, Swapped, Pending, etc.
    │   └── ProtectedRoute.jsx   # Auth Guard Redirecting to /login
    ├── pages/
    │   ├── Home.jsx             # Landing Page
    │   ├── Login.jsx            # Sign In Page with Demo Fillers
    │   ├── Register.jsx         # Student Registration Form
    │   ├── ExploreBooks.jsx     # Filterable Books Marketplace
    │   ├── BookDetails.jsx      # Detailed View with Buy/Swap Modals
    │   ├── AddBook.jsx          # Book Listing & Editing Form
    │   ├── Dashboard.jsx        # Student Dashboard Overview
    │   ├── MyListings.jsx       # Personal Book Management
    │   ├── Requests.jsx         # Received & Sent Proposals Management
    │   ├── ExchangeTracking.jsx # 5-Stage Visual Progress Tracker
    │   ├── Wishlist.jsx         # Saved Textbooks
    │   ├── Messages.jsx         # Two-Panel Messaging Simulation
    │   ├── SellerProfile.jsx    # Public Student Profile & Inventory
    │   ├── ProfileSettings.jsx  # Student Info & Avatar Configuration
    │   └── MyReviews.jsx        # Reviews Received & Submitted
    └── styles/
        ├── variables.css        # CSS Theme Custom Properties
        ├── global.css           # Global Reset, Typography, Buttons & Badges
        ├── components.css       # Component-level Styling
        └── responsive.css       # Mobile & Tablet Breakpoints
```

---

## 🔮 Future Java Backend Integration Plan

The frontend is architected with strict separation of concerns, making it trivial to connect to a **Java Spring Boot 3.x** REST API in the next phase:

```
[ React.js Frontend (Vite) ]
          │  REST JSON Requests (Axios / Fetch)
          ▼
[ Spring Boot Controller Layer ]
  - AuthController (/api/auth)
  - BookController (/api/books)
  - RequestController (/api/requests)
  - MessageController (/api/messages)
  - ReviewController (/api/reviews)
          │
[ Spring Service & Business Logic ]
          │
[ Spring Data JPA / Hibernate ]
          │
[ PostgreSQL / MySQL Database ]
```

### Planned Entity Mapping:
- **`User`**: `id`, `name`, `email`, `password_hash`, `college`, `department`, `semester`, `phone`, `avatar`, `rating`
- **`Book`**: `id`, `title`, `author`, `isbn`, `subject`, `semester`, `edition`, `condition`, `price`, `listing_type`, `seller_id`, `status`
- **`ExchangeRequest`**: `id`, `book_id`, `requester_id`, `seller_id`, `request_type`, `offered_book_id`, `tracking_step`, `status`, `notes`
- **`Message`**: `id`, `conversation_id`, `sender_id`, `recipient_id`, `content`, `timestamp`
- **`Review`**: `id`, `seller_id`, `reviewer_id`, `book_id`, `rating`, `comment`, `created_at`

---

## 🎓 Academic Demonstration Highlights

1. **Full User Journey**: Register → Login → Search/Filter → View Details → Propose Swap → Accept Proposal → Advance 5-Step Timeline → Rate Seller.
2. **Instant Demo Accounts**: Ready-to-click user buttons on `/login` to test both buyer and seller viewpoints without manual registration.
3. **Robust Fallbacks**: Even if external book cover images fail, the custom `BookCover` component renders an attractive gradient book cover with title and author.
4. **Data Persistence**: Everything added or updated persists in browser `localStorage`.
