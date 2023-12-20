# HiperBoot

RESTRICTIONS:
  IT IS CASE INSENSITIVE


TEST
    BASIC - Done
    CONTAINERS - H2, Postgres
    ENHANCE TESTS - Work In Progress
        - IN/NOT IN - ok
        - Equals/not -  ok
        - Greater Than - ok
        - Smaller than - ok
        - Between
        - LIKE
        - Null

        CREATE HELPERS
            Sorting Helpers
            Map helpers - Work in Progress (HBUtils)
        -----------
                return getPageByFilter(WaterHygieneCaseHistoryLog.class,
                        new HashMap<>(
                                Map.of("_page", new HashMap<>(Map.of("sort", "-created_at")), "water_hygiene_case", new HashMap<>(Map.of("id", id)))));
            }
        ---------------
REFACTORY / IMPROVEMENT (
    CONVERSION - done
    FILTER GENERATOR - 
    STOP USING .db.entity Package - 
    Use different data types than list (set, etc)) -
    USE DIFFERENT DATATYPES FOR PRIMARY KEY -
    Use COMPARABLE (see TODO in code)
    

TEST AS A LIB

DEFINE LICENCE

DOCUMENT ON GITHUB

PUT ON MAVEN
-------------------------
FAR IN THE FUTURE:
  - Enhanced joins, like Composed PK
  - 
