# AI Usage Report — BajriX Engineering Challenge

## 1. AI & Code-Generation Tools Used
- **Google Antigravity / Gemini 3.8**: Used as an AI pair-programming assistant across the project lifecycle.

## 2. Primary Use Cases
- **Scaffolding & Boilerplate**: Generating Java records for request/response DTOs, Spring Boot JPA repository methods, and Flyway migration schemas (`V1` through `V8`).
- **Domain Modeling**: Structuring the critical separation between canonical `Product` entities and multi-seller `SellerListing` offerings.
- **Frontend Component Structure**: Creating accessible React 18 UI components, responsive layout systems, and debounced search hooks.
- **Test Generation**: Drafting unit tests for `SellerListingService` covering boundary conditions, business rules, and optimistic locking exceptions.

## 3. Examples of AI Suggestions Corrected, Rejected, or Refined


### Example 1: Optimistic Locking Over Distributed Transactions
- **Initial AI Suggestion**: The AI proposed using pessimistic database row-level locking (`PESSIMISTIC_WRITE`) across listing update requests to handle concurrent edits.
- **Correction / Decision**: Replaced with JPA Optimistic Locking (`@Version` column in `seller_listings`). In a marketplace setting, multiple sellers updating the exact same listing concurrently is an infrequent conflict. Pessimistic locks would introduce unnecessary connection pool contention. The application maps `OptimisticLockException` to HTTP `409 Conflict`, and the React frontend prompts the user to refresh and retry.

### Example 2: Dual Authorization Scoping
- **Initial AI Suggestion**: A standard JWT interceptor was proposed that only checked whether the caller had a valid token with `SELLER` role.
- **Correction / Decision**: Refined the implementation to enforce query-level tenancy scoping. In addition to JWT validation, every database query scopes by `seller_id` (`findByIdAndSellerId`), and `SellerAuthInterceptor` cross-validates that the `X-Seller-Id` header strictly matches the authenticated seller's token claims. This guarantees that one seller cannot read, modify, or delete another seller's data even by manipulating request parameters.
