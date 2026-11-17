package com.bookloop.data;

import com.bookloop.model.*;

/**
 * Compulsory DummyData class providing realistic, high-fidelity Indian college mock data.
 * Features:
 * - 9 Realistic Indian student profiles from premier universities (DTU, BITS, IIT-B, VIT, NIT, etc.)
 * - 24 Diverse academic engineering/science textbook listings matching curriculum standards
 * - Realistic exchange and purchase requests covering all lifecycle statuses and tracking steps
 * - Verified peer ratings and reviews
 * - Automatic single-time initialization guard (prevents duplicate data creation)
 * - Complete reset capability (resetToDefault)
 */
public class DummyData {

    private static boolean isInitialized = false;

    /**
     * Initializes dummy data if not already populated.
     */
    public static synchronized void initialize(DataStore store) {
        if (isInitialized && !store.getAllBooks().isEmpty()) {
            return; // Guard against duplicate data loading
        }

        populateStudents(store);
        populateBooks(store);
        populateRequests(store);
        populateRatings(store);

        isInitialized = true;
    }

    /**
     * Resets the entire data store back to the clean baseline dummy records.
     */
    public static synchronized void resetToDefault(DataStore store) {
        store.clearAll();
        isInitialized = false;
        initialize(store);
        store.logActivity("System reset to baseline dummy dataset successfully.");
    }

    private static void populateStudents(DataStore store) {
        // 9 Realistic Indian College Students
        store.addStudent(new Student(
                "STU101", "Aarav Sharma", "aarav.sharma@dtu.ac.in", "+91 98765 43210",
                "2025-08-15", "Delhi Technological University (DTU)",
                "Computer Science and Engineering", 5, 4.9, 18, 12, 6
        ));

        store.addStudent(new Student(
                "STU102", "Priya Patel", "priya.patel@bits.ac.in", "+91 98123 45678",
                "2025-09-01", "BITS Pilani",
                "Information Technology", 4, 4.8, 14, 9, 5
        ));

        store.addStudent(new Student(
                "STU103", "Rohan Verma", "rohan.v@iitb.ac.in", "+91 98345 67890",
                "2025-07-20", "IIT Bombay",
                "Electrical Engineering", 6, 4.7, 11, 8, 3
        ));

        store.addStudent(new Student(
                "STU104", "Ananya Iyer", "ananya.iyer@vit.ac.in", "+91 97654 32109",
                "2025-10-10", "VIT Vellore",
                "Data Science & Artificial Intelligence", 3, 5.0, 16, 11, 5
        ));

        store.addStudent(new Student(
                "STU105", "Vikram Malhotra", "vikram.m@nitt.edu", "+91 98901 23456",
                "2025-08-28", "NIT Trichy",
                "Electronics & Communication Engineering", 5, 4.6, 8, 6, 4
        ));

        store.addStudent(new Student(
                "STU106", "Sneha Reddy", "sneha.reddy@iiit.ac.in", "+91 97112 34567",
                "2025-09-15", "IIIT Hyderabad",
                "Computer Science and Engineering", 4, 4.9, 12, 7, 5
        ));

        store.addStudent(new Student(
                "STU107", "Devansh Gupta", "devansh.g@nsut.ac.in", "+91 98223 45678",
                "2025-08-05", "Netaji Subhas University of Technology (NSUT)",
                "Mechanical Engineering", 6, 4.5, 9, 5, 2
        ));

        store.addStudent(new Student(
                "STU108", "Meera Nair", "meera.nair@manipal.edu", "+91 98456 78901",
                "2025-10-02", "Manipal Institute of Technology",
                "Biotechnology", 3, 4.8, 10, 8, 3
        ));

        store.addStudent(new Student(
                "STU109", "Kabir Mehta", "kabir.mehta@thapar.edu", "+91 98778 89900",
                "2025-09-22", "Thapar Institute of Engineering and Technology",
                "Civil Engineering", 5, 4.4, 7, 4, 2
        ));
    }

    private static void populateBooks(DataStore store) {
        // 24 Realistic Academic Engineering & Science Textbooks
        store.addBook(new Book(
                "BK201", "Introduction to Algorithms (CLRS)", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest",
                "978-0262033848", "Data Structures and Algorithms", "Computer Science", 3, "3rd Edition (MIT Press)",
                Condition.VERY_GOOD, 650.0, ListingType.BOTH,
                "Operating System Concepts by Silberschatz or Computer Networks by Tanenbaum",
                "Essential textbook for algorithms and data structures. Minimal pencil markings on greedy algorithms chapter, binding rock solid.",
                "STU102", "Priya Patel", "BITS Pilani", 4.8, "Active", "2026-10-15"
        ));

        store.addBook(new Book(
                "BK202", "Database System Concepts", "Abraham Silberschatz, Henry F. Korth, S. Sudarshan",
                "978-0078022159", "Database Management Systems", "Computer Science", 4, "7th Edition (McGraw Hill)",
                Condition.LIKE_NEW, 490.0, ListingType.FOR_SALE, "",
                "Standard textbook for DBMS covering relational model, SQL, indexing, and transactions. Zero pen marks.",
                "STU103", "Rohan Verma", "IIT Bombay", 4.7, "Active", "2026-10-18"
        ));

        store.addBook(new Book(
                "BK203", "Operating System Concepts (Dinosaur Book)", "Abraham Silberschatz, Peter B. Galvin, Greg Gagne",
                "978-1118063330", "Operating Systems", "Computer Science", 4, "9th Edition (Wiley)",
                Condition.GOOD, 380.0, ListingType.BOTH,
                "Computer Networking: A Top-Down Approach or Microprocessor 8085",
                "Classic OS bible. Comprehensive explanations of process scheduling, virtual memory, paging, and deadlocks.",
                "STU104", "Ananya Iyer", "VIT Vellore", 5.0, "Active", "2026-10-22"
        ));

        store.addBook(new Book(
                "BK204", "Computer Networks: A Systems Approach", "Larry L. Peterson, Bruce S. Davie",
                "978-0123850591", "Computer Networks", "Computer Science", 5, "5th Edition (Morgan Kaufmann)",
                Condition.VERY_GOOD, 420.0, ListingType.FOR_SALE, "",
                "Clear protocol-oriented approach from physical layer to application layer. Thorough coverage of TCP/IP.",
                "STU105", "Vikram Malhotra", "NIT Trichy", 4.6, "Active", "2026-10-25"
        ));

        store.addBook(new Book(
                "BK205", "Artificial Intelligence: A Modern Approach", "Stuart Russell, Peter Norvig",
                "978-0136042594", "Artificial Intelligence", "Computer Science", 6, "3rd Edition (Pearson)",
                Condition.LIKE_NEW, 750.0, ListingType.BOTH,
                "Deep Learning by Goodfellow or Pattern Recognition by Bishop",
                "Definitive guide to modern AI. Includes intelligent agents, search algorithms, logic, and planning.",
                "STU101", "Aarav Sharma", "DTU", 4.9, "Active", "2026-10-28"
        ));

        store.addBook(new Book(
                "BK206", "Pattern Recognition and Machine Learning", "Christopher M. Bishop",
                "978-0387310732", "Machine Learning", "Data Science", 6, "1st Edition (Springer)",
                Condition.VERY_GOOD, 680.0, ListingType.BOTH,
                "Artificial Intelligence by Russell & Norvig or Reinforcement Learning by Sutton",
                "In-depth Bayesian machine learning reference. Immaculate condition with hardcover.",
                "STU104", "Ananya Iyer", "VIT Vellore", 5.0, "Active", "2026-10-30"
        ));

        store.addBook(new Book(
                "BK207", "Higher Engineering Mathematics", "Dr. B.S. Grewal",
                "978-8174091956", "Engineering Mathematics", "Mathematics", 1, "44th Edition (Khanna Publishers)",
                Condition.GOOD, 350.0, ListingType.FOR_SALE, "",
                "Standard university math textbook covering calculus, differential equations, Laplace transforms, and linear algebra.",
                "STU103", "Rohan Verma", "IIT Bombay", 4.7, "Active", "2026-11-01"
        ));

        store.addBook(new Book(
                "BK208", "Digital Logic and Computer Design", "M. Morris Mano",
                "978-9332542525", "Digital Electronics", "Electronics", 3, "Revised Edition (Pearson)",
                Condition.GOOD, 280.0, ListingType.BOTH,
                "Signals and Systems by Oppenheim or Microelectronic Circuits",
                "Foundational digital logic book. Boolean algebra, logic gates, combinational and sequential circuit design.",
                "STU105", "Vikram Malhotra", "NIT Trichy", 4.6, "Active", "2026-11-02"
        ));

        store.addBook(new Book(
                "BK209", "Compilers: Principles, Techniques, and Tools (Dragon Book)", "Alfred V. Aho, Monica S. Lam, Ravi Sethi, Jeffrey D. Ullman",
                "978-0321486813", "Compiler Design", "Computer Science", 6, "2nd Edition (Pearson)",
                Condition.LIKE_NEW, 580.0, ListingType.FOR_SALE, "",
                "The legendary Dragon book on compiler construction: lexical analysis, syntax-directed translation, code optimization.",
                "STU106", "Sneha Reddy", "IIIT Hyderabad", 4.9, "Active", "2026-11-03"
        ));

        store.addBook(new Book(
                "BK210", "Java: The Complete Reference", "Herbert Schildt",
                "978-1260440232", "Object Oriented Programming", "Computer Science", 3, "11th Edition (Oracle Press)",
                Condition.VERY_GOOD, 520.0, ListingType.BOTH,
                "Effective Java by Joshua Bloch or Design Patterns (GoF)",
                "Comprehensive manual covering core Java syntax, OOP, Collections, Multithreading, and I/O streams.",
                "STU101", "Aarav Sharma", "DTU", 4.9, "Active", "2026-11-04"
        ));

        store.addBook(new Book(
                "BK211", "Signals and Systems", "Alan V. Oppenheim, Alan S. Willsky, S. Hamid Nawab",
                "978-0138147570", "Signals and Systems", "Electronics", 4, "2nd Edition (Prentice Hall)",
                Condition.GOOD, 460.0, ListingType.FOR_SALE, "",
                "The classic signal processing textbook. Continuous-time and discrete-time signals, Fourier analysis, Laplace & Z-transforms.",
                "STU103", "Rohan Verma", "IIT Bombay", 4.7, "Active", "2026-11-04"
        ));

        store.addBook(new Book(
                "BK212", "Computer Organization and Architecture", "William Stallings",
                "978-0134101613", "Computer Architecture", "Computer Science", 4, "10th Edition (Pearson)",
                Condition.VERY_GOOD, 410.0, ListingType.BOTH,
                "Microprocessor Architecture by Gaonkar or Modern Operating Systems",
                "Clean walkthrough of CPU cache hierarchy, instruction sets, pipelining, RISC vs CISC, and parallel processing.",
                "STU102", "Priya Patel", "BITS Pilani", 4.8, "Active", "2026-11-05"
        ));

        store.addBook(new Book(
                "BK213", "Software Engineering: A Practitioner's Approach", "Roger S. Pressman, Bruce R. Maxim",
                "978-0078022128", "Software Engineering", "Computer Science", 5, "8th Edition (McGraw Hill)",
                Condition.FAIR, 320.0, ListingType.FOR_SALE, "",
                "Agile processes, UML modeling, software testing, maintenance, and project metrics. Some annotations inside.",
                "STU106", "Sneha Reddy", "IIIT Hyderabad", 4.9, "Active", "2026-11-05"
        ));

        store.addBook(new Book(
                "BK214", "Theory of Computation: Formal Languages and Automata", "Michael Sipser",
                "978-1133187790", "Theory of Computation", "Computer Science", 5, "3rd Edition (Cengage)",
                Condition.LIKE_NEW, 530.0, ListingType.BOTH,
                "Compiler Design by Aho or Discrete Mathematics by Rosen",
                "Pristine copy of Sipser. Automata theory, Turing machines, decidability, and computational complexity (P vs NP).",
                "STU101", "Aarav Sharma", "DTU", 4.9, "Active", "2026-11-06"
        ));

        store.addBook(new Book(
                "BK215", "Let Us C", "Yashavant Kanetkar",
                "978-9387284494", "Programming in C", "Computer Science", 1, "17th Edition (BPB Publications)",
                Condition.GOOD, 180.0, ListingType.FOR_SALE, "",
                "Classic introductory C programming textbook for 1st year engineering undergraduates. All basic programs tested.",
                "STU107", "Devansh Gupta", "NSUT", 4.5, "Active", "2026-11-06"
        ));

        store.addBook(new Book(
                "BK216", "Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin (Uncle Bob)",
                "978-0132350884", "Software Engineering", "Computer Science", 6, "1st Edition (Prentice Hall)",
                Condition.LIKE_NEW, 620.0, ListingType.FOR_SALE, "",
                "Must-read programming book on writing readable, testable, maintainable code. No markings, like brand new.",
                "STU102", "Priya Patel", "BITS Pilani", 4.8, "Active", "2026-11-07"
        ));

        store.addBook(new Book(
                "BK217", "Discrete Mathematics and Its Applications", "Kenneth H. Rosen",
                "978-1259676512", "Discrete Mathematics", "Mathematics", 2, "8th Edition (McGraw Hill)",
                Condition.VERY_GOOD, 540.0, ListingType.BOTH,
                "Higher Engineering Mathematics by B.S. Grewal or Data Structures by Lipschutz",
                "Comprehensive discrete structures text: propositional logic, proofs, set theory, combinatorics, graph theory.",
                "STU104", "Ananya Iyer", "VIT Vellore", 5.0, "Active", "2026-11-07"
        ));

        store.addBook(new Book(
                "BK218", "Microprocessor Architecture, Programming and Applications with 8085", "Ramesh S. Gaonkar",
                "978-8187972884", "Microprocessors", "Electronics", 4, "6th Edition (Penram)",
                Condition.GOOD, 310.0, ListingType.FOR_SALE, "",
                "Covers 8085 pin diagram, assembly language programming, timing diagrams, memory interfacing, and interrupts.",
                "STU105", "Vikram Malhotra", "NIT Trichy", 4.6, "Active", "2026-11-08"
        ));

        store.addBook(new Book(
                "BK219", "Internal Combustion Engines", "V. Ganesan",
                "978-1259006197", "Mechanical Engineering", "Mechanical", 5, "4th Edition (McGraw Hill)",
                Condition.GOOD, 390.0, ListingType.FOR_SALE, "",
                "Standard textbook for thermodynamics of IC engines, fuel injection, air pollution, and dual combustion cycles.",
                "STU107", "Devansh Gupta", "NSUT", 4.5, "Active", "2026-11-08"
        ));

        store.addBook(new Book(
                "BK220", "Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides (GoF)",
                "978-0201633610", "Software Architecture", "Computer Science", 6, "1st Edition (Addison-Wesley)",
                Condition.LIKE_NEW, 540.0, ListingType.BOTH,
                "Clean Architecture by Robert Martin or Head First Design Patterns",
                "The classic Gang of Four handbook on creational, structural, and behavioral software design patterns.",
                "STU101", "Aarav Sharma", "DTU", 4.9, "Active", "2026-11-09"
        ));

        store.addBook(new Book(
                "BK221", "Cell and Molecular Biology: Concepts and Experiments", "Gerald Karp",
                "978-1118301791", "Biotechnology", "Biotechnology", 3, "7th Edition (Wiley)",
                Condition.VERY_GOOD, 480.0, ListingType.FOR_SALE, "",
                "Detailed biotechnology guide: membrane structure, bioenergetics, cellular communication, and genetics.",
                "STU108", "Meera Nair", "Manipal Institute", 4.8, "Active", "2026-11-09"
        ));

        store.addBook(new Book(
                "BK222", "Basic Structural Analysis", "C.S. Reddy",
                "978-0070702769", "Civil Engineering", "Civil", 4, "3rd Edition (Tata McGraw Hill)",
                Condition.GOOD, 360.0, ListingType.FOR_SALE, "",
                "Fundamental structural mechanics, deflection analysis, moment distribution method, and influence lines.",
                "STU109", "Kabir Mehta", "Thapar University", 4.4, "Active", "2026-11-10"
        ));

        store.addBook(new Book(
                "BK223", "Deep Learning", "Ian Goodfellow, Yoshua Bengio, Aaron Courville",
                "978-0262035613", "Deep Learning", "Data Science", 7, "1st Edition (MIT Press)",
                Condition.LIKE_NEW, 790.0, ListingType.BOTH,
                "Pattern Recognition and ML by Bishop or Computer Vision by Szeliski",
                "The deep learning textbook. Deep feedforward networks, regularization, optimization, CNNs, RNNs, and autoencoders.",
                "STU106", "Sneha Reddy", "IIIT Hyderabad", 4.9, "Active", "2026-11-10"
        ));

        store.addBook(new Book(
                "BK224", "Engineering Physics", "H.K. Malik, A.K. Singh",
                "978-0070671539", "Engineering Physics", "Physics", 1, "2nd Edition (McGraw Hill)",
                Condition.GOOD, 240.0, ListingType.FOR_SALE, "",
                "Covers optics, laser physics, quantum mechanics fundamentals, ultrasonics, and crystallography for 1st years.",
                "STU107", "Devansh Gupta", "NSUT", 4.5, "Active", "2026-11-11"
        ));
    }

    private static void populateRequests(DataStore store) {
        // Diverse requests with different statuses and tracking stages
        store.addRequest(new ExchangeRequest(
                "REQ301", "BK205", "Artificial Intelligence: A Modern Approach", 750.0,
                "STU101", "Aarav Sharma", "STU104", "Ananya Iyer", "Exchange",
                "BK206", "Pattern Recognition and Machine Learning", "2026-11-12",
                RequestStatus.PENDING, 1,
                "Hi Aarav, I would love to exchange my Bishop ML book for your AI Russell & Norvig copy! Can meet at library."
        ));

        store.addRequest(new ExchangeRequest(
                "REQ302", "BK210", "Java: The Complete Reference", 520.0,
                "STU101", "Aarav Sharma", "STU103", "Rohan Verma", "Purchase",
                null, "", "2026-11-10",
                RequestStatus.ACCEPTED, 3,
                "Need this for our 3rd semester Java OOPs practicals. Can pay UPI right away."
        ));

        store.addRequest(new ExchangeRequest(
                "REQ303", "BK220", "Design Patterns: Elements of Reusable Object-Oriented Software", 540.0,
                "STU101", "Aarav Sharma", "STU105", "Vikram Malhotra", "Purchase",
                null, "", "2026-11-08",
                RequestStatus.COMPLETED, 4,
                "Looking to prepare for software engineering campus placement interviews."
        ));

        store.addRequest(new ExchangeRequest(
                "REQ304", "BK201", "Introduction to Algorithms (CLRS)", 650.0,
                "STU102", "Priya Patel", "STU106", "Sneha Reddy", "Exchange",
                "BK209", "Compilers (Dragon Book)", "2026-11-05",
                RequestStatus.ACCEPTED, 2,
                "Happy to swap my Dragon Book for your CLRS algorithm book."
        ));

        store.addRequest(new ExchangeRequest(
                "REQ305", "BK207", "Higher Engineering Mathematics", 350.0,
                "STU103", "Rohan Verma", "STU107", "Devansh Gupta", "Purchase",
                null, "", "2026-11-03",
                RequestStatus.REJECTED, 1,
                "Offered lower price which was declined by seller."
        ));

        store.addRequest(new ExchangeRequest(
                "REQ306", "BK203", "Operating System Concepts (Dinosaur Book)", 380.0,
                "STU104", "Ananya Iyer", "STU101", "Aarav Sharma", "Exchange",
                "BK214", "Theory of Computation by Sipser", "2026-10-31",
                RequestStatus.COMPLETED, 5,
                "Book handed over at campus canteen. Exchanged successfully!"
        ));
    }

    private static void populateRatings(DataStore store) {
        // Realistic peer reviews
        store.addRating(new Rating(
                "REV401", "STU101", "STU105", "Vikram Malhotra", 5.0,
                "Smooth exchange! Aarav met near DTU library on time. Book was in top condition.", "2026-11-09"
        ));

        store.addRating(new Rating(
                "REV402", "STU101", "STU104", "Ananya Iyer", 5.0,
                "Great student seller. Textbooks match the photos and description exactly. Recommended!", "2026-11-01"
        ));

        store.addRating(new Rating(
                "REV403", "STU102", "STU101", "Aarav Sharma", 4.8,
                "Priya packed the book very nicely and shipped quickly via student courier. Thank you!", "2026-10-25"
        ));

        store.addRating(new Rating(
                "REV404", "STU103", "STU107", "Devansh Gupta", 4.5,
                "Decent transaction. Clear communication and honest about minor highlighter marks.", "2026-10-28"
        ));

        store.addRating(new Rating(
                "REV405", "STU104", "STU106", "Sneha Reddy", 5.0,
                "Prompt responses and very polite exchange. Both of us saved a lot of money on college texts.", "2026-10-18"
        ));
    }
}
