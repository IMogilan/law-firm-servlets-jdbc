# law-firm-servlets-jdbc

# HOW TO RUN APPLICATION
# 1. Create database law_firm (first line in create-tables-script.sql)
# 2. Created tables in law_firm by execution of create-tables-script.sql
# 3. Populate tables by execution of populate-script.sql
# 4. Edit properties in application.properties file (if necessary)
# 5. Set up and launch Tomcat 10 

# Эндпоинт	        	        Метод	Описание
# /api/clients/			        GET	    Получение списка всех клиентов
# /api/clients/{id}		        GET	    Получение информации о конкретном клиенте
# /api/clients/			        POST	Создание нового клиента
# /api/clients/{id}		        PUT	    Обновление информации о клиенте
# /api/clients/{id}		        DELETE	Удаление клиента
# /api/law-firms/			    GET	    Получение списка всех юр. фирм
# /api/law-firms/{id}		    GET	    Получение информации о конкретной юр фирме
# /api/law-firms/			    POST	Создание новой юр. фирмы
# /api/law-firms/{id}		    PUT	    Обновление информации о юр. фирме
# /api/law-firms/{id}		    DELETE	Удаление юр. фирмы
# /api/lawyers/			        GET	    Получение списка всех юристов
# /api/lawyers/{id}		        GET	    Получение информации о конкретном юристе
# /api/lawyers/			        POST	Создание нового юриста
# /api/lawyers/{id}		        PUT	    Обновление информации о юристе
# /api/lawyers/{id}		        DELETE	Удаление юриста
# /api/lawyers/{id}/tasks/	    GET	    Получение списка всех задач у конкретного юриста
# /api/lawyers/{id}/tasks/{id}	GET	    Получение информации о конкретной задаче у конкретного юриста
# /api/lawyers/{id}/tasks/	    POST	Создание новой задачи у конкретного юриста
# /api/lawyers/{id}/tasks/{id}	PUT	    Обновление информации о задаче у конкретного юриста
# /api/lawyers/{id}/tasks/{id}	DELETE	Удаление задачи у конкретного юриста
# /api/contact-details/		    GET	    Получение списка всех контактов юристов
# /api/contact-details/{id}	    GET	    Получение информации о контактах конкретного юриста
# /api/contact-details/		    POST	Создание новых контактов
# /api/contact-details/{id}	    PUT	    Обновление информации о контактах
# /api/contact-details/{id}	    DELETE	Удаление контактов
# /api/tasks/			        GET	    Получение списка всех задач
# /api/tasks/{id}			    GET	    Получение информации о конкретной задаче
# /api/tasks/			        POST	Создание новой задачи
# /api/tasks/{id}			    PUT	    Обновление информации о задаче
# /api/tasks/{id}			    DELETE	Удаление задачи
# /api/tasks/{id}/lawyers/	    GET	    Получение списка всех юристов, ответственных за выполнение конкретной задачи
# /api/tasks/{id}/lawyers/{id}	GET	    Получение информации о конкретном юристе, ответственном за выполнение конкретной задачи
# /api/tasks/{id}/lawyers/	    POST	Создание нового юриста, ответственного за выполнение конкретной задачи
# /api/tasks/{id}/lawyers/{id}	PUT	    Обновление информации о юристе, ответственном за выполнение конкретной задачи
# /api/tasks/{id}/lawyers/{id}	DELETE	Удаление юриста, ответственного за выполнение конкретной задачи