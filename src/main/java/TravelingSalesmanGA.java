import java.util.Arrays;
import java.util.Random;

public class TravelingSalesmanGA {

    // Nombres de las ciudades principales de Colombia
    private static final String[] cities = {
            "Bogota", "Medellin", "Cali", "Barranquilla", "Cartagena", "Bucaramanga", "Cucuta"
    };

    // Matriz de distancias entre ciudades principales de Colombia (en kilómetros)
    private static final int[][] distanceMatrix = {
            {0, 415, 462, 1002, 1036, 396, 555},   // Bogotá
            {415, 0, 414, 704, 643, 335, 391},   // Medellín
            {462, 414, 0, 1204, 1262, 688, 838},   // Cali
            {1002, 704, 1204, 0, 125, 842, 914},   // Barranquilla
            {1036, 643, 1262, 125, 0, 726, 830},   // Cartagena
            {396, 335, 688, 842, 726, 0, 204},   // Bucaramanga
            {555, 391, 838, 914, 830, 204, 0}    // Cúcuta
    };

    private static final int POPULATION_SIZE = 10;
    private static final int NUM_GENERATIONS = 100;

    public static void main(String[] args) {
        int[][] population = createInitialPopulation();

        for (int i = 0; i < NUM_GENERATIONS; i++) {
            population = evolvePopulation(population);
        }

        int[] bestRoute = findBestRoute(population);
        System.out.println("Mejor ruta encontrada: " + Arrays.toString(convertRouteToCityNames(bestRoute)));
        System.out.println("Distancia de la mejor ruta: " + calculateRouteDistance(bestRoute));
        System.out.println("URL de Google Maps para visualizar la ruta:");
        System.out.println(generateGoogleMapsURL(bestRoute));
    }

    /**
     * Crea la población inicial para el algoritmo genético,
     * generando rutas aleatorias para cada individuo.
     *
     * @return Una matriz bidimensional donde cada fila representa una ruta (individuo) en la población.
     */
    private static int[][] createInitialPopulation() {
        int[][] population = new int[POPULATION_SIZE][distanceMatrix.length];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = createRandomRoute();
        }
        return population;
    }

    /**
     * Genera una ruta aleatoria en forma de arreglo de índices de ciudades.
     *
     * @return Un arreglo que representa una ruta aleatoria entre las ciudades.
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
     * Mezcla los elementos de un arreglo en orden aleatorio.
     *
     * @param array Arreglo de enteros que representa una ruta a mezclar.
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
     * Evoluciona la población actual aplicando selección, cruce y mutación.
     *
     * @param population La población actual de rutas.
     * @return Una nueva población generada.
     */
    private static int[][] evolvePopulation(int[][] population) {
        int[][] newPopulation = new int[population.length][population[0].length];
        Random rand = new Random();
        double mutationRate = 0.1;
        int tournamentSize = 5;

        for (int i = 0; i < population.length; i++) {
            int[] parent1 = tournamentSelection(population, tournamentSize);
            int[] parent2 = tournamentSelection(population, tournamentSize);
            int[] child = crossover(parent1, parent2);
            newPopulation[i] = child;
        }

        for (int[] individual : newPopulation) {
            if (rand.nextDouble() < mutationRate) {
                mutate(individual);
            }
        }

        return newPopulation;
    }

    /**
     * Selección de individuos mediante torneo para el cruce.
     *
     * @param population     La población de rutas.
     * @param tournamentSize Tamaño del torneo para selección.
     * @return La mejor ruta seleccionada en el torneo.
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
     * Cruza dos rutas para crear una nueva ruta (hijo).
     *
     * @param parent1 La primera ruta padre.
     * @param parent2 La segunda ruta padre.
     * @return Una ruta generada a partir del cruce de los padres.
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

    /**
     * Verifica si una ruta ya contiene una ciudad.
     *
     * @param child La ruta a verificar.
     * @param gene  El índice de la ciudad.
     * @return Verdadero si la ciudad ya está en la ruta, falso de lo contrario.
     */
    private static boolean containsGene(int[] child, int gene) {
        for (int g : child) {
            if (g == gene) return true;
        }
        return false;
    }

    /**
     * Realiza una mutación intercambiando dos ciudades en una ruta.
     *
     * @param individual La ruta a mutar.
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
     * @return La distancia total de la ruta.
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
     * Encuentra la mejor ruta en la población actual (la de menor distancia).
     *
     * @param population La población de rutas.
     * @return La ruta con la menor distancia en la población.
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
     * Convierte una ruta de índices de ciudades a nombres de ciudades.
     *
     * @param route La ruta a convertir.
     * @return Un arreglo de nombres de ciudades correspondientes a la ruta.
     */
    private static String[] convertRouteToCityNames(int[] route) {
        String[] cityNames = new String[route.length];
        for (int i = 0; i < route.length; i++) {
            cityNames[i] = cities[route[i]];
        }
        return cityNames;
    }

    /**
     * Genera una URL de Google Maps para visualizar la ruta.
     *
     * @param route La ruta para la cual generar la URL.
     * @return La URL de Google Maps con la ruta.
     */
    private static String generateGoogleMapsURL(int[] route) {
        StringBuilder url = new StringBuilder("https://www.google.com/maps/dir/");
        for (int i : route) {
            url.append(cities[i]).append("/");
        }
        url.append(cities[route[0]]);
        return url.toString();
    }
}
