import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Random;

public class TravelingSalesmanGA {

    // Nombres de las ciudades
    private static final String[] cities = {
            "Bogota", "Medellin", "Cali", "Barranquilla", "Cartagena", "Bucaramanga", "Cucuta"
    };

    // Matriz de distancias entre las ciudades
    private static final int[][] distanceMatrix = {
            {0, 415, 462, 1002, 1036, 396, 555},
            {415, 0, 414, 704, 643, 335, 391},
            {462, 414, 0, 1204, 1262, 688, 838},
            {1002, 704, 1204, 0, 125, 842, 914},
            {1036, 643, 1262, 125, 0, 726, 830},
            {396, 335, 688, 842, 726, 0, 204},
            {555, 391, 838, 914, 830, 204, 0}
    };

    // Diferentes combinaciones de parámetros para la evaluación
    private static final int[][] PARAMETER_COMBINATIONS = {
            {10, 50},    // Combinación rápida: población pequeña y pocas generaciones
            {30, 200},   // Combinación intermedia: población moderada y más generaciones
            {100, 500}   // Combinación exhaustiva: población grande y muchas generaciones
    };

    public static void main(String[] args) {
        int[] baseRoute = createRandomRoute(); // Ruta base para todas las evaluaciones
        System.out.println("Ruta base inicial: " + Arrays.toString(convertRouteToCityNames(baseRoute)));

        // Iteración a través de las combinaciones de parámetros
        for (int[] params : PARAMETER_COMBINATIONS) {
            int populationSize = params[0];
            int numGenerations = params[1];

            System.out.println("\nParámetros: POPULATION_SIZE = " + populationSize + ", NUM_GENERATIONS = " + numGenerations);

            long startTime = System.nanoTime(); // Marca el inicio del cómputo

            // Inicializa la población con clones de la ruta base
            int[][] population = new int[populationSize][];
            for (int i = 0; i < populationSize; i++) {
                population[i] = baseRoute.clone(); // Cada individuo empieza con la misma ruta base
            }

            // Evoluciona la población a través de generaciones
            for (int i = 0; i < numGenerations; i++) {
                population = evolvePopulation(population);
            }

            int[] bestRoute = findBestRoute(population); // Obtiene la mejor ruta de la población final
            long endTime = System.nanoTime(); // Marca el final del cómputo

            double executionTimeInSeconds = (endTime - startTime) / 1e9; // Calcula el tiempo de ejecución

            // Muestra los resultados de la mejor ruta encontrada y el tiempo de ejecución
            System.out.println("Mejor ruta encontrada: " + Arrays.toString(convertRouteToCityNames(bestRoute)));
            System.out.println("Distancia de la mejor ruta: " + calculateRouteDistance(bestRoute));
            System.out.println("Tiempo de cómputo: " + executionTimeInSeconds + " segundos");

            // Genera la URL de Google Maps para la ruta
            String googleMapsURL = generateGoogleMapsURL(bestRoute);
            System.out.println("URL de Google Maps para visualizar la ruta:");
            System.out.println(googleMapsURL);

            // Abre la URL en el navegador predeterminado
            openInBrowser(googleMapsURL);
        }
    }

    /**
     * Crea una ruta aleatoria que representa un camino entre todas las ciudades.
     *
     * @return Un array que contiene el índice de las ciudades en orden aleatorio.
     */
    private static int[] createRandomRoute() {
        int[] route = new int[distanceMatrix.length];
        for (int i = 0; i < route.length; i++) {
            route[i] = i;
        }
        shuffleArray(route);
        return route;
    }

    /**
     * Método auxiliar para mezclar un array, creando así una permutación aleatoria.
     *
     * @param array El array a mezclar.
     */
    private static void shuffleArray(int[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Evoluciona la población aplicando selección, cruce y mutación.
     *
     * @param population La población actual de rutas.
     * @return La nueva población evolucionada.
     */
    private static int[][] evolvePopulation(int[][] population) {
        int[][] newPopulation = new int[population.length][];
        for (int i = 0; i < population.length; i++) {
            int[] parent1 = tournamentSelection(population);
            int[] parent2 = tournamentSelection(population);
            int[] offspring = crossover(parent1, parent2);
            mutate(offspring);
            newPopulation[i] = offspring;
        }
        return newPopulation;
    }

    /**
     * Selecciona un individuo de la población mediante un torneo.
     *
     * @param population La población de rutas.
     * @return Una ruta seleccionada.
     */
    private static int[] tournamentSelection(int[][] population) {
        Random rand = new Random();
        int index1 = rand.nextInt(population.length);
        int index2 = rand.nextInt(population.length);
        return calculateRouteDistance(population[index1]) < calculateRouteDistance(population[index2]) ? population[index1] : population[index2];
    }

    /**
     * Realiza el cruce entre dos rutas.
     *
     * @param parent1 La primera ruta padre.
     * @param parent2 La segunda ruta padre.
     * @return La ruta descendiente generada.
     */
    private static int[] crossover(int[] parent1, int[] parent2) {
        int[] child = new int[parent1.length];
        Arrays.fill(child, -1);
        Random rand = new Random();
        int start = rand.nextInt(parent1.length);
        int end = rand.nextInt(parent1.length - start) + start;
        for (int i = start; i < end; i++) {
            child[i] = parent1[i];
        }
        int childIndex = end;
        for (int i = 0; i < parent2.length; i++) {
            int city = parent2[(end + i) % parent2.length];
            if (!containsCity(child, city)) {
                child[childIndex % child.length] = city;
                childIndex++;
            }
        }
        return child;
    }

    /**
     * Verifica si una ciudad está en la ruta.
     *
     * @param route La ruta.
     * @param city  La ciudad a verificar.
     * @return Verdadero si la ciudad está en la ruta, falso en caso contrario.
     */
    private static boolean containsCity(int[] route, int city) {
        for (int i : route) {
            if (i == city) return true;
        }
        return false;
    }

    /**
     * Realiza una mutación aleatoria en la ruta.
     *
     * @param route La ruta a mutar.
     */
    private static void mutate(int[] route) {
        Random rand = new Random();
        int index1 = rand.nextInt(route.length);
        int index2 = rand.nextInt(route.length);
        int temp = route[index1];
        route[index1] = route[index2];
        route[index2] = temp;
    }

    /**
     * Calcula la distancia total de una ruta.
     *
     * @param route La ruta a evaluar.
     * @return La distancia total de la ruta.
     */
    private static int calculateRouteDistance(int[] route) {
        int distance = 0;
        for (int i = 0; i < route.length - 1; i++) {
            distance += distanceMatrix[route[i]][route[i + 1]];
        }
        // Asegura que la ciudad final regrese a la ciudad de inicio
        distance += distanceMatrix[route[route.length - 1]][route[0]];
        return distance;
    }

    /**
     * Encuentra la mejor ruta en la población.
     *
     * @param population La población de rutas.
     * @return La ruta con la menor distancia.
     */
    private static int[] findBestRoute(int[][] population) {
        int[] bestRoute = population[0];
        int bestDistance = calculateRouteDistance(bestRoute);
        for (int[] route : population) {
            int currentDistance = calculateRouteDistance(route);
            if (currentDistance < bestDistance) {
                bestRoute = route;
                bestDistance = currentDistance;
            }
        }
        return bestRoute;
    }

    /**
     * Convierte una ruta de índices en nombres de ciudades.
     *
     * @param route La ruta en índices.
     * @return La ruta en nombres de ciudades.
     */
    private static String[] convertRouteToCityNames(int[] route) {
        String[] cityNames = new String[route.length];
        for (int i = 0; i < route.length; i++) {
            cityNames[i] = cities[route[i]];
        }
        return cityNames;
    }

    /**
     * Genera una URL de Google Maps para la ruta dada.
     *
     * @param route La ruta de ciudades.
     * @return La URL de Google Maps para visualizar la ruta.
     */
    private static String generateGoogleMapsURL(int[] route) {
        StringBuilder url = new StringBuilder("https://www.google.com/maps/dir/");
        for (int i = 0; i < route.length; i++) {
            url.append(cities[route[i]].replace(" ", "+"));
            if (i < route.length - 1) {
                url.append("/");
            }
        }
        // Asegura que la ruta vuelva a la ciudad de inicio
        url.append("/").append(cities[route[0]].replace(" ", "+"));
        return url.toString();
    }

    /**
     * Abre una URL en el navegador predeterminado.
     *
     * @param url La URL a abrir.
     */
    private static void openInBrowser(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El navegador no se puede abrir en esta plataforma.");
        }
    }
}
