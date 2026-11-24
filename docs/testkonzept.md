# Testkonzept GamesLibrary

Stand: 24.11.2026 (erste Fassung, Auftrag 5.2). Das Konzept beschreibt, was heute getestet wird. Geplantes ist als «geplant» markiert und wird nachgeführt, bevor der Testcode entsteht.

## 1. Teststrategie

### Testobjekt

Testobjekt ist unsere GamesLibrary auf Basis des Handouts `bbw-md/GamesLibraryHandout` (Plan B laut Projektauftrag).

| Teil | Ordner | Technik | Im Test |
|---|---|---|---|
| Backend | `backend` (einziger Maven-Ordner mit `mvnw`) | Spring Boot 4.1, Java 26, H2 | ja |
| Frontend | `frontend` | React, Cypress | nur die bestehenden Cypress-Tests des Handouts |

Abzudeckende Funktionen im Backend:

- Spiele auflisten: `GET /api/games`
- Spiel per ID abrufen: `GET /api/games/{id}`
- Spiele nach Titel suchen: `GET /api/games/search?title=...`
- Spiel anlegen, ändern, löschen: `POST`, `PUT`, `DELETE /api/games`
- Titel normalisieren: `TitleNormalizer`

Nicht im Umfang: der Steam-Import (`SteamController`), weil er einen fremden Dienst braucht, und Last- oder Sicherheitstests.

### Risiken

Bewertung: Wahrscheinlichkeit und Schaden je niedrig, mittel oder hoch.

| Nr. | Risiko | Wahrsch. | Schaden | Teststufe |
|---|---|---|---|---|
| R1 | Die Logik im `GameService` ist falsch, z. B. wird eine unbekannte ID nicht als Fehler gemeldet | mittel | hoch | Unit-Test |
| R2 | Ein Endpunkt liefert den falschen Statuscode, z. B. 500 statt 400 bei einer fehlerhaften Anfrage | hoch | mittel | Integrationstest |
| R3 | Die Suche liefert falsche Treffer, weil die Datenbankabfrage nicht stimmt | mittel | hoch | Integrationstest |
| R4 | Titel werden uneinheitlich gespeichert (Leerzeichen, Kleinschreibung, leer) | mittel | niedrig | Unit-Test |
| R5 | Ein kaputter Stand landet auf `main`, ohne dass es jemand merkt | mittel | hoch | Pipeline |

R2 ist bereits eingetreten: `GET /api/games/search` ohne Parameter und `GET /api/games/abc` liefern 500. Das wird als Issue gemeldet (Auftrag 5.3).

### Teststufen

| Stufe | Prüft | Werkzeug | Stand |
|---|---|---|---|
| Unit-Test | eine Klasse, das Repository ist gemockt | JUnit 5, Mockito | vorhanden: `GameServiceTest` (9 Tests), `TitleNormalizerTest` (6 Testfälle) |
| Integrationstest | Controller, Service, Repository und H2 zusammen | `@SpringBootTest`, MockMvc | vorhanden: `BackendApplicationTests` (Kontext startet). Geplant: `GameApiIntegrationTest` (Auftrag 5.3) |
| Schnittstellentest | Endpunkte von aussen | Postman, `@WebMvcTest` | geplant (Aufträge 6.1, 6.2) |
| End-to-End-Test | ganze Anwendung im Browser | Cypress | vorhanden im Handout (`frontend/cypress/e2e`), läuft nicht in der Pipeline |

Der Schwerpunkt liegt auf Unit-Tests, weil sie schnell sind und die Logik direkt prüfen. Integrationstests decken ab, was Unit-Tests nicht sehen: Statuscodes, JSON und die echte Datenbankabfrage.

## 2. Rollen und Verantwortlichkeiten

| Person | Verantwortung |
|---|---|
| Timo Eichenberger | (ergänzen) |
| (Name ergänzen) | (ergänzen) |

Beide schreiben Tests und committen unter eigenem Konto. Wird bis zur Schlussabgabe ergänzt.

## 3. Infrastruktur

| Umgebung | Zweck | Datenbank |
|---|---|---|
| Lokal (Notebook, JDK 26, `./mvnw`) | Entwickeln und Tests starten | H2 im Speicher |
| GitHub Actions (`ubuntu-latest`, Temurin 26) | Tests bei jedem Push und Pull Request | H2 im Speicher |

- Testdaten: `backend/src/main/resources/data.sql` wird bei jedem Start in die leere H2-Datenbank geladen. Unit-Tests bauen ihre Daten selbst.
- Werkzeuge: JUnit 5, Mockito, Spring Boot Test, Maven-Wrapper, GitHub Actions. Geplant: JaCoCo (5.3), Postman (6.1).
- Die Tests brauchen keinen laufenden Fremddienst und keine installierte Datenbank.

## 4. Entwicklungsvorgehen und Testobjekte

- **TDD:** Neue Logik entsteht in Zyklen Rot, Grün, Refactor. Jeder Zyklus hinterlässt einen Commit `rot: …` mit dem Test und einen Commit `grün: …` mit der Umsetzung. So ist `TitleNormalizer` in vier Zyklen entstanden.
- **Branches:** Gearbeitet wird auf einem Feature-Branch. Auf `main` kommt Code nur über einen Pull Request.
- **Pipeline:** `.github/workflows/ci.yml` startet bei jedem Push und Pull Request. Der Job `test` führt `./mvnw -B test` für jeden Maven-Ordner der Matrix aus (bei uns `backend`).
- **Deploy nur bei grünen Tests:** Der Job `deploy` hat `needs: test` und läuft erst, wenn alle Test-Jobs grün sind. Bei einem roten Test wird er übersprungen. Deploy ist vorerst ein Platzhalter.

## 5. Testfallerstellung

Erste Fassung, wird bis zur Schlussabgabe ergänzt. Bisher verwendet:

- **Äquivalenzklassen:** Bei `TitleNormalizer` gültige Titel (mit Rand-Leerzeichen, mit mehrfachen Leerzeichen, klein geschrieben) und ungültige Titel (`null`, leer, nur Leerzeichen).
- **Positive und negative Fälle:** Bei `GameService` je Methode der Erfolgsfall und der Fall «ID existiert nicht».
- **Benennung:** `methode_ergebnis_bedingung`, z. B. `getGameById_throwsNoSuchElementException_whenNotFound`.

## 6. Testfalldurchführung

Erste Fassung, wird bis zur Schlussabgabe ergänzt.

- Lokal vor jedem Push: `./mvnw test` im Ordner `backend`.
- In der Pipeline: automatisch bei jedem Push und Pull Request.
- Abnahmekriterien bisher: Alle Tests sind grün, und der Lauf auf `main` ist grün. Ein Pull Request wird erst nach einem grünen Lauf gemergt.
- Ein Schwellwert für die Abdeckung wird festgelegt, sobald JaCoCo läuft (geplant, Auftrag 5.3).

## 7. Fehlerbehandlung

Erste Fassung, wird bis zur Schlussabgabe ergänzt.

- Fehler werden als GitHub-Issue gemeldet. Die Vorlage `.github/ISSUE_TEMPLATE/bug.md` ist geplant (Auftrag 5.3).
- Schweregrade: niedrig (störend, Funktion nutzbar), mittel (Funktion liefert ein falsches Ergebnis, es gibt einen Umweg), hoch (Funktion nicht nutzbar oder Daten falsch).
- Ein Test, der mal grün und mal rot ist, wird repariert oder begründet entfernt, nicht ignoriert.
