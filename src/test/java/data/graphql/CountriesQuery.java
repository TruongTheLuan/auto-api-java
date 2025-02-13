package data.graphql;

public class CountriesQuery {
    public static final String GET_COUNTRIES_QUERY_DATA = """
                {
                  "data": {
                    "ca": {
                      "name": "Canada",
                      "native": "Canada",
                      "capital": "Ottawa",
                      "emoji": "🇨🇦",
                      "currency": "CAD",
                      "languages": [
                        {
                          "code": "en",
                          "name": "English"
                        },
                        {
                          "code": "fr",
                          "name": "French"
                        }
                      ]
                    }
                  }
                }""";
    public static final String GET_COUNTRIES_QUERY = """
            query GetCountries($caCode: ID!) {
              ca: country(code: $caCode) {
                name
                native
                capital
                emoji
                currency
                languages {
                  code
                  name
                }
              }
            }
            """;
}
