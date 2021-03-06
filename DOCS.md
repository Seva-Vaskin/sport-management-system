# Руководство по использованию программы
### Запуск
Приложение имеет графический интерфейс.
Программу можно запустить запуском установочной программы на Windows или запуском appImage на Linux.

### Проверка работоспособности
Для упрощения тестирования к программе также написаны генераторы различных данных для проверки работоспособности. Все они находятся в директории ``oop-2021-sport-management-system-sekira/src/test/kotlin/``. Отдельно стоит отметить файл ``CheckPointProtocolGenerating.kt``, который позволяет по проведённой жеребьёвке виртуально провести соревнования. Очень удобно удобно для подделки результатов.

### Предполагаемый сценарий использования программы
1. После запуска программы предлагается загрузить уже существующую H2 базу данных либо создать новую. В дальнейшем все операции будут выполняться с данными в этой базе.
2. Затем вам следует загрузить информацию об имени и дате соревнования во вкладке `events` либо заполнить ее на месте.
3. Аналогично загружаются данные о контрольных точках во вкладке `checkpoints`, о возможных маршрутах во вкладке `routes`, о группах участников во вкладке `groups`, и наконец, о командах участников во вкладке `teams`.
4. После чего в выпадающем меню `navigate` выбрать `toss` -- сформируются списки участников с их временем старта.
5. Дальше вам предлагается загрузить данные о том, как участники достигли контрольных точек во вкладке `timestamps`.
6. Здесь в меню `navigate` можете вернуться назад кнопкой `Rollback`, если нужно откорректировать какие-то данные, либо сформировать результаты кнопкой `Result`
7. После формирования результатов все нарушившие правила участники будут дисквалифицированы, а остальным в пределах группы вычислится время отставания от лидера, место в группе и штрафные очки.
8. Так же вычислятся очки команды по результатам участников.
