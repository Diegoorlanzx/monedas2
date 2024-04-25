import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            try {
                // Obtener la tasa de cambio actual desde la API
                URL url = new URL("https://v6.exchangerate-api.com/v6/af0e7d358f40c639f04f8c23/latest/USD");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("OCURRIO UN ERROR");
                } else {
                    StringBuilder informationString = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while (scanner.hasNext()) {
                        informationString.append(scanner.nextLine());
                    }
                    scanner.close();

                    // Parsear el String JSON
                    JSONObject jsonObject = new JSONObject(informationString.toString());
                    JSONObject conversionRates = jsonObject.getJSONObject("conversion_rates");

                    // Mostrar opciones al usuario
                    System.out.println("Opciones:");
                    System.out.println("1. De dólar a peso argentino");
                    System.out.println("2. De peso argentino a dólar");
                    System.out.println("3. De dólar a real brasileño");
                    System.out.println("4. De dólar a peso colombiano");
                    System.out.println("5. De peso colombiano a dólar");
                    System.out.println("6. Salir");
                    System.out.println("7. Consultar otro tipo de moneda");

                    int opcion;
                    do {
                        try {
                            // Solicitar la opción al usuario
                            System.out.print("Selecciona una opción: ");
                            opcion = inputScanner.nextInt();
                            inputScanner.nextLine(); // Consumir la nueva línea después del entero

                            // Verificar si la opción ingresada es válida
                            if (opcion < 1 || opcion > 7) {
                                System.out.println("Opción no válida. Por favor, selecciona una opción del 1 al 7.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Solo se permiten valores numéricos.");
                            inputScanner.nextLine(); // Limpiar el buffer de entrada
                            opcion = 0; // Establecer una opción inválida para repetir el bucle
                        }
                    } while (opcion < 1 || opcion > 7);

                    switch (opcion) {
                        case 1:
                            convertirMoneda(inputScanner, conversionRates, "USD", "ARS");
                            break;
                        case 2:
                            convertirMoneda(inputScanner, conversionRates, "ARS", "USD");
                            break;
                        case 3:
                            convertirMoneda(inputScanner, conversionRates, "USD", "BRL");
                            break;
                        case 4:
                            convertirMoneda(inputScanner, conversionRates, "USD", "COP");
                            break;
                        case 5:
                            convertirMoneda(inputScanner, conversionRates, "COP", "USD");
                            break;
                        case 6:
                            continuar = false;
                            break;
                        case 7:
                            // Solicitar al usuario ingresar la moneda de origen y destino
                            System.out.print("Ingresa la moneda de origen (ej. USD, EUR, GBP): ");
                            String monedaOrigen = inputScanner.nextLine().toUpperCase();
                            System.out.print("Ingresa la moneda de destino (ej. USD, EUR, GBP): ");
                            String monedaDestino = inputScanner.nextLine().toUpperCase();
                            convertirMoneda(inputScanner, conversionRates, monedaOrigen, monedaDestino);
                            break;
                        default:
                            System.out.println("Opción no válida.");
                            break;
                    }
                }
            } catch (IOException | JSONException exception) {
                exception.printStackTrace();
            }
        }

        //  salir del bucle
        inputScanner.close();
    }

    private static void convertirMoneda(Scanner inputScanner, JSONObject conversionRates, String monedaOrigen, String monedaDestino) {
        try {
            // Validar que monedas esten en las tasas de conversión
            if (!conversionRates.has(monedaOrigen) || !conversionRates.has(monedaDestino)) {
                System.out.println("Moneda no encontrada en las tasas de conversión.");
                return;
            }

            // Obtener la tasa de conversión de la moneda original a la moneda de destino
            double tasaConversion = conversionRates.getDouble(monedaDestino) / conversionRates.getDouble(monedaOrigen);

            // Solicitar al usuario ingresar la cantidad
            System.out.print("Ingresa la cantidad: ");
            double cantidad = inputScanner.nextDouble();
            inputScanner.nextLine();

            // Calcular la conversión y mostrar el resultado
            double conversion = cantidad * tasaConversion;
            System.out.printf("%.2f %s equivale a %.2f %s\n", cantidad, monedaOrigen, conversion, monedaDestino);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}