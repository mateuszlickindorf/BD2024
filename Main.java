package entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        // a) Wyświetl procent produktów, które dostarczają więcej niż 50% sumy żelaza i wapnia. Zwróć wynik do dwóch miejsc po przecinku.

        var queryStringA = "SELECT ROUND(100.0 * COUNT(p) / ((SELECT COUNT (p2) FROM ProductsEntity p2)), 2) FROM ProductsEntity p WHERE p.calcium + p.iron > 50 ";
        Query queryA = em.createQuery(queryStringA);
        Float resultA = (Float) queryA.getSingleResult();
        System.out.println(String.format("Procent produktów spełniających warunek z podpunktu a): %s%%  ", resultA));

        // b) Wyświetl średnią wartość kaloryczną produktów z bekonem w nazwie.

        var queryStringB = "SELECT AVG(p.calories) FROM ProductsEntity p WHERE p.itemName LIKE '%bacon%'";
        Query queryB = em.createQuery(queryStringB);
        Double resultB = (Double) queryB.getSingleResult();
        System.out.println(String.format("\nŚrednia wartość kaloryczna produktów z bekonem w nazwie z podpunktu b): %skcal  ", resultB));

        // c) Dla każdej z kategorii wyświetl produkt o największej wartości cholesterolu

        // Śniadania
        var queryStringC1 = "SELECT p.itemName, p.cholesterole FROM ProductsEntity p WHERE p.category = \"CAT1\" ORDER BY p.cholesterole DESC";
        var queryStringC2 = "SELECT p.itemName, p.cholesterole FROM ProductsEntity p WHERE p.category = \"CAT7\" ORDER BY p.cholesterole DESC";
        var queryStringC3 = "SELECT p.itemName, p.cholesterole FROM ProductsEntity p WHERE p.category = \"CAT8\" ORDER BY p.cholesterole DESC";

        System.out.println("\nNajwięcej cholesterolu w kategorii Śniadania ma/mają:");
        printObjectsWithHighestCholesterole(em, queryStringC1);

        System.out.println("\nNajwięcej cholesterolu w kategorii Napoje ma/mają:");
        printObjectsWithHighestCholesterole(em, queryStringC2);

        System.out.println("\nNajwięcej cholesterolu w kategorii Kawy i Herbaty ma/mają:");
        printObjectsWithHighestCholesterole(em, queryStringC3);
//
        // d) Wyświetl liczbę kaw (Mocha lub Coffee w nazwie) niezawierających błonnika.

        var queryStringD = "SELECT COUNT(p) FROM ProductsEntity p WHERE (p.itemName LIKE '%Mocha%' OR p.itemName LIKE '%Coffee%') AND p.fiber = 0";
        Query queryD = em.createQuery(queryStringD);
        Long resultD = (Long) queryD.getSingleResult();
        System.out.printf("\n Liczba kaw (z Mocha lub Coffee w nazwie) niezawierających błonnika (podpunkt d)):%s", resultD);

        // e) Wyświetl kaloryczność wszystkich McMuffinów. Wyniki wyświetl w kilodżulach (jedna
        //kaloria to 4184 dżule) rosnąco.

        var queryStringE = "SELECT p.itemName, p.calories * 4184 FROM ProductsEntity p WHERE p.itemName LIKE '%McMuffin%' ORDER BY calories ASC";
        Query queryE = em.createQuery(queryStringE);
        List<Object> resultE = (List<Object>) queryE.getResultList();
        System.out.printf("\n \nKaloryczność McMuffinów [kJ] (podpunkt e):\n");
        for (Object obj : resultE) {
            Object[] array = (Object[]) obj;
            String value = Objects.toString(array[1], "");
            String name = Objects.toString(array[0], "");
            System.out.println("\tNazwa produktu: " + name + "\tKalorie[kJ]: " + value);
        }

        // f) Wyświetl liczbę różnych wartości węglowodanów.

        var queryStringF = "SELECT COUNT(DISTINCT p.carbs) FROM ProductsEntity p";
        Query queryF = em.createQuery(queryStringF);
        Long resultF = (Long) queryF.getSingleResult();
        System.out.printf("\nLiczba różnych wartości węglowodanów (podpunkt e)): %s ", resultF);
    }


    private static void printObjectsWithHighestCholesterole (EntityManager em, String queryString) {
        Query queryStringtoQuery = em.createQuery(queryString);
        List<Object> ResultsList = (List<Object>) queryStringtoQuery.getResultList();
        Object highestValueObject = ResultsList.get(0);
        Object[] highestValueArray = (Object[]) highestValueObject;
        String highestValue = Objects.toString(highestValueArray[1], "");
        for (Object obj : ResultsList) {
            if (obj instanceof Object[] && ((Object[]) obj).length == 2) {
                Object[] array = (Object[]) obj;
                String value = Objects.toString(array[1], "");

                if (value.equals(highestValue)) {
                    String name = Objects.toString(array[0], "");
                    System.out.println("\tNazwa produktu: " + name);
                    System.out.println("\tZawartość cholesterolu [mg]: " + value);
                }
            }
        }
    }
}

