# HiperBoot

RESTRICTIONS:
  IT IS CASE INSENSITIVE


TEST
    BASIC - Done
    CONTAINERS - H2, Postgres
    ENHANCE TESTS - Work In Progress
        Filters
            - IN/NOT IN - ok
            - Equals/not -  ok
            - Greater Than - ok
            - Smaller than - ok
            - Between - ok
            - LIKE - Ok
            - Null - Ok
            
        Joins
            - ManyToOne - done
            - OneToMany - done
        Sort - 
        Pagination - 


    
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
    FILTER GENERATOR - wip
    Use COMPARABLE (see TODO in code)
    

TEST AS A LIB

DEFINE LICENCE

DOCUMENT ON GITHUB

PUT ON MAVEN
-------------------------
FAR IN THE FUTURE:
  - Enhanced joins, like Composed PK
  - 
