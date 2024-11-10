# Algoritmo Genético para el Problema del Agente Viajero

Este proyecto implementa una solución para el **Problema del Viajante de Comercio** usando un **Algoritmo Genético**. El objetivo es encontrar la ruta más corta posible que un viajante debe seguir para visitar varias ciudades principales de Colombia, regresando a su ciudad de origen.

## Descripción del Problema

El problema del Viajante de Comercio (TSP, por sus siglas en inglés) es un problema clásico en el que se busca determinar la ruta óptima para que un viajante recorra un conjunto de ciudades, visitando cada una de ellas una única vez y volviendo al punto de partida, con el objetivo de minimizar la distancia total recorrida.

Este problema se vuelve computacionalmente difícil de resolver a medida que aumenta el número de ciudades. Por ello, en lugar de explorar todas las rutas posibles, este proyecto utiliza un **algoritmo genético** para aproximarse a una solución eficiente en un tiempo razonable.

## ¿Qué es un Algoritmo Genético?

Un **Algoritmo Genético** es una técnica de optimización inspirada en la selección natural, donde las soluciones evolucionan para mejorar generación tras generación. Los conceptos clave en un algoritmo genético son:
- **Población**: Un conjunto de posibles rutas.
- **Selección**: La elección de las mejores rutas para la reproducción.
- **Cruce**: La combinación de rutas para generar nuevas rutas.
- **Mutación**: La modificación aleatoria de rutas para explorar nuevas posibilidades.

En este contexto, cada ruta representa una posible solución al problema de encontrar el recorrido más corto entre las ciudades.

## Estructura del Código

- **Cities y Distance Matrix**: Se define una lista de las principales ciudades de Colombia y una matriz de distancias entre ellas en kilómetros.
- **Población Inicial**: Se crean rutas aleatorias como punto de partida.
- **Evolución de Población**: Mediante selección, cruce y mutación, se generan nuevas rutas a lo largo de múltiples generaciones para mejorar la solución.
- **Ruta Óptima**: La mejor ruta encontrada se muestra con los nombres de las ciudades y se calcula su distancia total.
- **Visualización en Google Maps**: El programa genera una URL para ver la ruta en Google Maps.

## Ejemplo de Ciudades

Este proyecto incluye una lista de ciudades y sus distancias entre sí:
```plaintext
1. Bogotá
2. Medellín
3. Cali
4. Barranquilla
5. Cartagena
6. Bucaramanga
7. Cúcuta
```

### Distancias (Ejemplos)

- Bogotá a Medellín: 415 km
- Medellín a Cali: 414 km
- Barranquilla a Cartagena: 125 km

## Ejecución

### Requisitos

- Java 8 o superior
- Compilador de Java (javac)

### Compilación y Ejecución

1. Compilar el programa:

    ```bash
    javac TravelingSalesmanGA.java
    ```

2. Ejecutar el programa:

    ```bash
    java TravelingSalesmanGA
    ```

### Ejemplo de Salida

El programa imprimirá la mejor ruta encontrada entre las ciudades, la distancia total y un enlace de Google Maps para visualizar la ruta.

```plaintext
Mejor ruta encontrada: [Bogotá, Medellín, Bucaramanga, Cúcuta, Cali, Cartagena, Barranquilla]
Distancia de la mejor ruta: 2100 km
URL de Google Maps para visualizar la ruta:
https://www.google.com/maps/dir/Bogota/Medellin/Bucaramanga/Cucuta/Cali/Cartagena/Barranquilla/Bogota


