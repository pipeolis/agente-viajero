import java.util.Arrays;
import java.util.Random;

public class TravelingSalesmanGA {

    private static final String[] cities = {
            "Bogota", "Medellin", "Cali", "Barranquilla", "Cartagena", "Bucaramanga", "Cucuta"
    };

    private static final int[][] distanceMatrix = {
            {0, 415, 462, 1002, 1036, 396, 555},    //Bogota
            {415, 0, 414, 704, 643, 335, 391},      //Medellin
            {462, 414, 0, 1204, 1262, 688, 838},    //Cali
            {1002, 704, 1204, 0, 125, 842, 914},    //Barranquilla
            {1036, 643, 1262, 125, 0, 726, 830},    //Cartagena
            {396, 335, 688, 842, 726, 0, 204},      //Bucaramanga
            {555, 391, 838, 914, 830, 204, 0}       //Cucuta
    };

    // Define combinaciones de parámetros para evaluar su impacto en el tiempo de cómputo y calidad de resultados
    private static final int[][] PARAMETER_COMBINATIONS = {
            {10, 50},    // Combinación rápida
            {30, 200},   // Combinación intermedia
            {100, 500}   // Combinación exhaustiva
    };

    public static void main(String[] args) {
        // Itera a través de diferentes combinaciones de parámetros para observar su impacto en tiempo y resultados
        for (int[] params : PARAMETER_COMBINATIONS) {
            int populationSize = params[0];
            int numGenerations = params[1];
            System.out.println("\n############################################################################################################");
            System.out.println("\nParámetros: POPULATION_SIZE = " + populationSize + ", NUM_GENERATIONS = " + numGenerations);

            long startTime = System.nanoTime(); // Marca el inicio del cómputo

            int[][] population = createInitialPopulation(populationSize); // Genera la población inicial

            for (int i = 0; i < numGenerations; i++) {
                population = evolvePopulation(population); // Evoluciona la población por un número de generaciones
            }

            int[] bestRoute = findBestRoute(population); // Obtiene la mejor ruta de la población final
            long endTime = System.nanoTime(); // Marca el final del cómputo

            double executionTimeInSeconds = (endTime - startTime) / 1e9; // Calcula el tiempo de ejecución

            // Muestra los resultados de la mejor ruta encontrada y el tiempo de ejecución
            System.out.println("Mejor ruta encontrada: " + Arrays.toString(convertRouteToCityNames(bestRoute)));
            System.out.println("Distancia de la mejor ruta: " + calculateRouteDistance(bestRoute));
            System.out.println("Tiempo de cómputo: " + executionTimeInSeconds + " segundos");
            System.out.println("URL de Google Maps para visualizar la ruta:");
            System.out.println(generateGoogleMapsURL(bestRoute));
        }
    }

    /**
     * Crea la población inicial con rutas aleatorias.
     *
     * @param populationSize El tamaño de la población.
     * @return Un array 2D que representa la población inicial.
     */
    private static int[][] createInitialPopulation(int populationSize) {
        int[][] population = new int[populationSize][distanceMatrix.length];
        for (int i = 0; i < populationSize; i++) {
            population[i] = createRandomRoute();
        }
        return population;
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
     * @param population La población actual.
     * @return La nueva población después de una generación.
     */
    private static int[][] evolvePopulation(int[][] population) {
        int[][] newPopulation = new int[population.length][population[0].length];
        Random rand = new Random();
        double mutationRate = 0.1; // Tasa de mutación
        int tournamentSize = 5; // Tamaño del torneo de selección

        // Selección y cruce
        for (int i = 0; i < population.length; i++) {
            int[] parent1 = tournamentSelection(population, tournamentSize);
            int[] parent2 = tournamentSelection(population, tournamentSize);
            int[] child = crossover(parent1, parent2);
            newPopulation[i] = child;
        }

        // Mutación
        for (int[] individual : newPopulation) {
            if (rand.nextDouble() < mutationRate) {
                mutate(individual);
            }
        }

        return newPopulation;
    }

    /**
     * Selecciona un individuo mediante torneo.
     *
     * @param population La población actual.
     * @param tournamentSize Tamaño del torneo.
     * @return El mejor individuo seleccionado.
     */
    private static int[] tournamentSelection(int[][] population, int tournamentSize) {
        Random rand = new Random();
        int[][] tournament = new int[tournamentSize][];

        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = rand.nextInt(population.length);
            tournament[i] = population[randomIndex];
        }

        int[] best = tournament[0];
        int bestDistance = calculateRouteDistance(best);
        for (int i = 1; i < tournamentSize; i++) {
            int currentDistance = calculateRouteDistance(tournament[i]);
            if (currentDistance < bestDistance) {
                best = tournament[i];
                bestDistance = currentDistance;
            }
        }

        return best;
    }

    /**
     * Cruza dos padres para producir un hijo.
     *
     * @param parent1 El primer padre.
     * @param parent2 El segundo padre.
     * @return El hijo resultante.
     */
    private static int[] crossover(int[] parent1, int[] parent2) {
        Random rand = new Random();
        int numCities = parent1.length;
        int[] child = new int[numCities];
        Arrays.fill(child, -1);

        int start = rand.nextInt(numCities);
        int end = rand.nextInt(numCities - start) + start;

        if (end - start >= 0) System.arraycopy(parent1, start, child, start, end - start);

        int childIndex = end;
        for (int i = 0; i < numCities; i++) {
            int gene = parent2[i];
            if (!containsGene(child, gene)) {
                if (childIndex >= numCities) childIndex = 0;
                child[childIndex++] = gene;
            }
        }

        return child;
    }

    private static boolean containsGene(int[] child, int gene) {
        for (int g : child) {
            if (g == gene) return true;
        }
        return false;
    }

    /**
     * Aplica mutación a un individuo.
     *
     * @param individual El individuo a mutar.
     */
    private static void mutate(int[] individual) {
        Random rand = new Random();
        int index1 = rand.nextInt(individual.length);
        int index2 = rand.nextInt(individual.length);

        int temp = individual[index1];
        individual[index1] = individual[index2];
        individual[index2] = temp;
    }

    /**
     * Calcula la distancia total de una ruta.
     *
     * @param route La ruta a calcular.
     * @return La distancia total.
     */
    private static int calculateRouteDistance(int[] route) {
        int distance = 0;
        for (int i = 0; i < route.length - 1; i++) {
            distance += distanceMatrix[route[i]][route[i + 1]];
        }
        distance += distanceMatrix[route[route.length - 1]][route[0]];
        return distance;
    }

    /**
     * Encuentra la mejor ruta en una población.
     *
     * @param population La población actual.
     * @return La mejor ruta encontrada.
     */
    private static int[] findBestRoute(int[][] population) {
        int[] bestRoute = population[0];
        int bestDistance = calculateRouteDistance(bestRoute);

        for (int i = 1; i < population.length; i++) {
            int currentDistance = calculateRouteDistance(population[i]);
            if (currentDistance < bestDistance) {
                bestRoute = population[i];
                bestDistance = currentDistance;
            }
        }

        return bestRoute;
    }

    /**
     * Convierte una ruta de índices a nombres de ciudades.
     *
     * @param route La ruta en índices.
     * @return La ruta en nombres de ciudades.
     */
    private static String[] convertRouteToCityNames(int[] route) {
        String[] cityRoute = new String[route.length];
        for (int i = 0; i < route.length; i++) {
            cityRoute[i] = cities[route[i]];
        }
        return cityRoute;
    }

    /**
     * Genera una URL de Google Maps para visualizar la ruta.
     *
     * @param route La ruta en índices.
     * @return La URL de Google Maps.
     */
    private static String generateGoogleMapsURL(int[] route) {
        StringBuilder url = new StringBuilder("https://www.google.com/maps/dir/");
        for (int cityIndex : route) {
            url.append(cities[cityIndex]).append("/");
        }
        url.append(cities[route[0]]); // Para cerrar el ciclo de la ruta
        return url.toString();
    }
}
