# fp-graphql

GraphQL codegen plugin and runtime library for Team Foreldrepenger backends.

## Shared context

- Source of truth for shared domain, architecture, and conventions: `navikt/fp-context`
- Copilot Space: `navikt/TeamForeldrepenger`

## Repo-specific context

| Topic                | Details                                                                                        |
|----------------------|------------------------------------------------------------------------------------------------|
| Role                 | Generates Java client-side POJOs from GraphQL schemas and provides the matching runtime support |
| Consumers            | `vtp`, `fp-felles` and `fp-inntektsmelding`                                                    |
| Tech stack           | Java, Maven, FreeMarker templates                                                        |
| Notes                | Supports both Jackson 2 and Jackson 3 generation paths                                         |

README.md contains plugin options and usage instructions for the plugin and runtime library.

## Verification

- On interface or plugin changes: May verify using a local build in the consumers. Then run `fp-autotest` suite `verdikjede`.
