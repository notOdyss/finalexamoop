# Task Manager - OOP Final Project

Полнофункциональное приложение для управления задачами, разработанное с использованием JavaFX и PostgreSQL.

## Описание проекта

Task Manager - это десктопное приложение для управления задачами и проектами. Проект демонстрирует применение объектно-ориентированного программирования, MVC паттерна, работу с базами данных и современный UI/UX дизайн.

## Технологический стек

- **Java 21** - Основной язык программирования
- **JavaFX 21** - Графический интерфейс
- **PostgreSQL** - Реляционная база данных
- **HikariCP** - Connection pool для оптимальной работы с БД
- **BCrypt** - Хеширование паролей
- **Maven** - Система сборки проекта

## Архитектура проекта

Проект следует **MVC (Model-View-Controller)** паттерну:

```
src/
├── main/
│   ├── java/
│   │   └── org/example/workspace/
│   │       ├── models/           # Model - модели данных
│   │       │   ├── User.java
│   │       │   ├── Task.java
│   │       │   └── TaskStatus.java
│   │       ├── controllers/      # Controller - бизнес-логика
│   │       │   ├── WelcomeController.java
│   │       │   ├── LoginController.java
│   │       │   ├── RegisterController.java
│   │       │   ├── DashboardController.java
│   │       │   ├── TaskFormController.java
│   │       │   ├── StatisticsController.java
│   │       │   └── ProfileController.java
│   │       ├── dao/              # Data Access Object - работа с БД
│   │       │   ├── UserDAO.java
│   │       │   └── TaskDAO.java
│   │       ├── database/         # Управление БД
│   │       │   └── DatabaseManager.java
│   │       ├── utils/            # Утилиты
│   │       │   ├── SceneManager.java
│   │       │   ├── Session.java
│   │       │   └── AlertUtil.java
│   │       └── Launcher.java     # Точка входа
│   └── resources/                # View - представления (FXML + CSS)
│       ├── WelcomePage.fxml
│       ├── LoginPage.fxml
│       ├── RegisterPage.fxml
│       ├── Dashboard.fxml
│       ├── TaskForm.fxml
│       ├── Statistics.fxml
│       ├── Profile.fxml
│       └── main.css
```

## Функциональность

### Основные возможности:

1. **Аутентификация**
   - Регистрация новых пользователей
   - Вход в систему с хешированием паролей (BCrypt)
   - Управление сессией пользователя

2. **CRUD операции с задачами**
   - Создание новых задач
   - Просмотр списка задач в таблице
   - Редактирование задач
   - Удаление задач

3. **Фильтрация и поиск**
   - Поиск задач по названию и описанию
   - Фильтрация по статусу (To Do, In Progress, Completed, Cancelled)
   - Просмотр просроченных задач

4. **Статистика**
   - Общее количество задач
   - Распределение по статусам
   - Процент выполнения
   - Визуализация через PieChart

5. **Профиль пользователя**
   - Просмотр информации профиля
   - Редактирование username и email
   - Смена пароля
   - Удаление аккаунта

## Java Collections

Проект активно использует Java Collections:

- **ArrayList** - для хранения списков задач
- **HashMap** - для кэширования задач в TaskDAO
- **Map** - для статистики задач
- **ObservableList** - для JavaFX TableView

## Требования для запуска

### Необходимое ПО:

1. **Java Development Kit (JDK) 21+**
   - Скачать: https://www.oracle.com/java/technologies/downloads/

2. **PostgreSQL 13+**
   - Скачать: https://www.postgresql.org/download/

3. **Maven 3.8+**
   - Обычно идет вместе с IDE (IntelliJ IDEA, Eclipse)

## Настройка базы данных

### 1. Создание базы данных

Запустите PostgreSQL и выполните:

```sql
-- Создать базу данных
CREATE DATABASE taskmanager_db;

-- Подключиться к базе данных
\c taskmanager_db

-- Таблицы создаются автоматически при запуске приложения
-- Но если нужно создать вручную:

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(10) DEFAULT 'MEDIUM',
    deadline DATE,
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_deadline ON tasks(deadline);
```

### 2. Настройка подключения

Откройте файл `DatabaseManager.java` и измените параметры подключения:

```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/taskmanager_db";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "123456"; // ВАШ ПАРОЛЬ
```

## Запуск проекта

### Вариант 1: Через Maven (командная строка)

```bash
# Перейти в директорию проекта
cd Workspace

# Запустить приложение
mvn clean javafx:run
```

### Вариант 2: Через IDE (IntelliJ IDEA)

1. Откройте проект в IntelliJ IDEA
2. Дождитесь загрузки Maven зависимостей
3. Найдите класс `Launcher.java`
4. Нажмите правой кнопкой → Run 'Launcher.main()'

### Вариант 3: Через Maven Plugin

```bash
# Скомпилировать проект
mvn clean compile

# Запустить
mvn javafx:run
```

## Использование приложения

### Первый запуск:

1. **Регистрация**
   - Нажмите "Register" на главном экране
   - Введите username (мин. 3 символа)
   - Введите email
   - Введите пароль (мин. 6 символов)
   - Подтвердите пароль

2. **Вход**
   - Введите username и пароль
   - Нажмите "Login"

3. **Создание задачи**
   - На Dashboard нажмите "+ New Task"
   - Заполните форму:
     - Название задачи (обязательно)
     - Описание (опционально)
     - Статус (по умолчанию "To Do")
     - Приоритет (Low/Medium/High)
     - Дедлайн (опционально)
   - Нажмите "Save Task"

4. **Управление задачами**
   - Используйте поиск для нахождения задач
   - Фильтруйте по статусу
   - Нажимайте "Edit" для редактирования
   - Нажимайте "Delete" для удаления

5. **Просмотр статистики**
   - Нажмите "Statistics" для просмотра аналитики
   - Смотрите распределение задач по статусам
   - Отслеживайте процент выполнения

## Особенности реализации

### ООП принципы:

- **Инкапсуляция**: Все поля классов private с геттерами/сеттерами
- **Наследование**: Controllers наследуют от базовых JavaFX классов
- **Полиморфизм**: Использование интерфейсов и абстрактных методов
- **Абстракция**: Разделение на слои (DAO, Service, Controller)

### Design Patterns:

- **MVC (Model-View-Controller)**: Основная архитектура
- **Singleton**: Session для управления текущим пользователем
- **DAO (Data Access Object)**: Изоляция логики работы с БД
- **Factory**: SceneManager для создания сцен

### Безопасность:

- Хеширование паролей с использованием BCrypt
- Prepared Statements для защиты от SQL-инъекций
- Валидация входных данных на уровне контроллеров

## Структура базы данных

### Таблица `users`
- `id` - Уникальный идентификатор
- `username` - Имя пользователя (уникальное)
- `email` - Email (уникальный)
- `password` - Хешированный пароль
- `created_at` - Дата создания аккаунта

### Таблица `tasks`
- `id` - Уникальный идентификатор
- `title` - Название задачи
- `description` - Описание задачи
- `status` - Статус (TODO, IN_PROGRESS, COMPLETED, CANCELLED)
- `priority` - Приоритет (LOW, MEDIUM, HIGH)
- `deadline` - Дедлайн выполнения
- `user_id` - ID пользователя (внешний ключ)
- `created_at` - Дата создания
- `updated_at` - Дата последнего обновления

## Авторы

Проект разработан как финальная работа по курсу Object-Oriented Programming.

## Лицензия

Образовательный проект для демонстрации навыков программирования.

---

**Примечание**: Для корректной работы убедитесь, что PostgreSQL запущен и доступен на порту 5432.
