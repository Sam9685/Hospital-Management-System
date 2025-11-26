# Hospital Management System

A comprehensive full-stack hospital management system built with Angular 19 and Spring Boot, featuring patient registration, appointment booking, payment processing, complaint management, and admin panel.

## 🚀 Features

### Patient Features
- **User Registration & Login**: Secure patient registration with validation
- **Appointment Booking**: Search and book appointments with doctors
- **Appointment Management**: View, reschedule, and cancel appointments
- **Payment Processing**: Secure payment gateway integration
- **Complaint Management**: Register and track complaints
- **Profile Management**: Update personal information

### Admin Features
- **Doctor Management**: Add, update, and manage doctor profiles
- **Appointment Management**: View and manage all appointments
- **Complaint Management**: Handle and resolve patient complaints
- **Dashboard**: Analytics and reporting
- **User Management**: Manage patient accounts

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA**
- **H2 Database** (in-memory for development)
- **Maven** for dependency management

### Frontend
- **Angular 19**
- **Angular Material** for UI components
- **TypeScript**
- **RxJS** for reactive programming
- **CSS3** with modern styling

## 📁 Project Structure

```
hospital-management-system/
├── backend/
│   ├── src/main/java/com/example/SpringDemo/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic
│   │   └── security/       # Security configuration
│   └── pom.xml
├── frontend/
│   └── hospital-management-frontend/
│       ├── src/app/
│       │   ├── core/       # Core services and models
│       │   ├── features/   # Feature modules
│       │   └── shared/     # Shared components
│       └── package.json
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Maven 3.6 or higher
- Angular CLI 19

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Install dependencies:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend/hospital-management-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on `http://localhost:4200`

## 🔐 Authentication

The system uses JWT (JSON Web Token) authentication with the following features:
- Secure token-based authentication
- Role-based access control (ADMIN, PATIENT)
- Token expiration handling
- Automatic token refresh

## 📊 Database Schema

### Key Entities
- **Users**: Patient and admin user accounts
- **Doctors**: Doctor profiles with specializations
- **Appointments**: Patient appointments with doctors
- **Payments**: Payment transactions
- **Complaints**: Patient complaints and feedback
- **Specializations**: Medical specializations

## 🎨 UI/UX Features

- **Modern Design**: Clean, professional interface with blue and white theme
- **Responsive Layout**: Works on desktop, tablet, and mobile devices
- **Material Design**: Consistent UI components using Angular Material
- **User-Friendly**: Intuitive navigation and clear user flows
- **Accessibility**: WCAG compliant design patterns

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - Patient registration

### Doctors
- `GET /api/doctors` - Get all doctors
- `POST /api/doctors` - Create doctor (Admin only)
- `GET /api/doctors/search` - Search doctors

### Appointments
- `GET /api/appointments` - Get appointments
- `POST /api/appointments` - Create appointment
- `PUT /api/appointments/{id}` - Update appointment

### Specializations
- `GET /api/specializations` - Get specializations
- `POST /api/specializations` - Create specialization (Admin only)

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file:
```bash
mvn clean package
```

2. Run the JAR:
```bash
java -jar target/hospital-management-system-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
1. Build for production:
```bash
npm run build
```

2. Deploy the `dist/` folder to your web server

## 🧪 Testing

### Backend Testing
```bash
mvn test
```

### Frontend Testing
```bash
npm test
```

## 📝 Development Notes

- The system uses H2 in-memory database for development
- JWT tokens expire after 24 hours
- All API responses follow a consistent format
- Frontend uses standalone components (Angular 19 feature)
- Material Design components for consistent UI

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions, please contact the development team or create an issue in the repository.

---

**Note**: This is a development version of the hospital management system. For production use, additional security measures, database configuration, and deployment considerations should be implemented.
