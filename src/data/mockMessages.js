export const MOCK_MESSAGES = [
  {
    conversationId: 'conv_1_2',
    participants: ['user_1', 'user_2'],
    withUser: {
      id: 'user_2',
      name: 'Priya Patel',
      college: 'BITS Pilani',
      avatar: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80',
      online: true,
      lastSeen: 'Online'
    },
    lastMessage: 'Sounds good! Let us meet tomorrow at 4 PM near the student activity center.',
    lastMessageTime: '11:42 AM',
    unreadCount: 1,
    messages: [
      {
        id: 'm1',
        senderId: 'user_1',
        senderName: 'Aarav Sharma',
        text: 'Hey Priya! I saw your CLRS 3rd Edition listing. Is it still available for exchange?',
        time: '11:15 AM',
        date: '2026-11-13'
      },
      {
        id: 'm2',
        senderId: 'user_2',
        senderName: 'Priya Patel',
        text: 'Hi Aarav! Yes it is! I saw you offered Ian Goodfellow’s Deep Learning. I would love to make that swap.',
        time: '11:22 AM',
        date: '2026-11-13'
      },
      {
        id: 'm3',
        senderId: 'user_1',
        senderName: 'Aarav Sharma',
        text: 'Awesome! The deep learning book is in like-new condition, no markings at all.',
        time: '11:30 AM',
        date: '2026-11-13'
      },
      {
        id: 'm4',
        senderId: 'user_2',
        senderName: 'Priya Patel',
        text: 'Sounds good! Let us meet tomorrow at 4 PM near the student activity center.',
        time: '11:42 AM',
        date: '2026-11-13'
      }
    ]
  },
  {
    conversationId: 'conv_1_3',
    participants: ['user_1', 'user_3'],
    withUser: {
      id: 'user_3',
      name: 'Rohan Verma',
      college: 'IIT Bombay',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80',
      online: false,
      lastSeen: '1 hour ago'
    },
    lastMessage: 'Accepted your request! I have sent you my UPI ID for payment.',
    lastMessageTime: 'Yesterday',
    unreadCount: 0,
    messages: [
      {
        id: 'm10',
        senderId: 'user_3',
        senderName: 'Rohan Verma',
        text: 'Hello Aarav! I got your purchase request for Database System Concepts.',
        time: '04:10 PM',
        date: '2026-11-12'
      },
      {
        id: 'm11',
        senderId: 'user_1',
        senderName: 'Aarav Sharma',
        text: 'Great! Are you okay with sending it via campus courier or should I arrange a pickup?',
        time: '04:18 PM',
        date: '2026-11-12'
      },
      {
        id: 'm12',
        senderId: 'user_3',
        senderName: 'Rohan Verma',
        text: 'Accepted your request! I have sent you my UPI ID for payment.',
        time: '04:30 PM',
        date: '2026-11-12'
      }
    ]
  },
  {
    conversationId: 'conv_1_4',
    participants: ['user_1', 'user_4'],
    withUser: {
      id: 'user_4',
      name: 'Ananya Iyer',
      college: 'VIT Vellore',
      avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80',
      online: true,
      lastSeen: 'Online'
    },
    lastMessage: 'Can you please share a photo of the spine of the Russell & Norvig book?',
    lastMessageTime: 'Nov 11',
    unreadCount: 0,
    messages: [
      {
        id: 'm20',
        senderId: 'user_4',
        senderName: 'Ananya Iyer',
        text: 'Hi Aarav, really interested in your AI book!',
        time: '02:00 PM',
        date: '2026-11-11'
      },
      {
        id: 'm21',
        senderId: 'user_4',
        senderName: 'Ananya Iyer',
        text: 'Can you please share a photo of the spine of the Russell & Norvig book?',
        time: '02:05 PM',
        date: '2026-11-11'
      }
    ]
  }
];

