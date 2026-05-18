## Contribution
- Nguyễn Đỗ Ánh Dương (Database): Database components in `db` package; first builds of `model` package and some `DAO` classes in `repository` package such as `UserRepository`, `UniversalLogRepository`, `LoginHistoryRepository` and `AmenityRepository`.
- Nguyễn Sơn Hải (Frontend): All `ui` components: Table models, customized classes extend Swing (e.g `StyledButton`, `StyledComboBox` ...), and UI helpers like column sorter and update in real time.
- Nguyễn Đình Thắng (Backend): remaining `repository` packages; final builds for `POJO` and `DTO` classes; All `service` classes for business logic and `util` package.
  
...

## Design Decisions

### Why use inheritance instead of if-else?

* Encapsulation of behavior  
With inheritance, each subclass (e.g., `StyledButton`, `StyledComboBox`, or different `Repository` classes) encapsulates its own behavior. A giant if-else block is not neccessary to check what type of object the system is dealing with — the right method is automatically called via polymorphism.

* Readability and maintainability
`if-else` chains quickly become messy when there are many conditions. Inheritance organizes logic into separate classes, making the code easier to read and maintain.

* Extensibility
Adding a new feature with `if-else` means editing existing code and risking bugs. With inheritance, creating a new subclass and override the necessary methods is much more simple — no need to touch the old code.

* Aligns with OOP principles  
Your project already uses POJOs, DTOs, DAOs, and services. Inheritance fits naturally into this architecture, ensuring consistency across backend, frontend, and database layers.
---

### What was the most difficult part?
