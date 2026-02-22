# 📐 Especificación Técnica: Editor de Polígonos Geográficos
> **Versión**: 1.0.4 | **Módulo**: Inventario & Mapas | **Tipo**: ECU (Especificación de Componentes)

---

## 1. Motor de Trazado Geográfico

El Editor de Polígonos permite a los administradores digitalizar planos físicos sobre la interfaz del ERP para habilitar la interactividad de los lotes.

### Flujo de Definición:
1.  **Selección**: Se elige un activo de la lista lateral (Lotes sin polígono).
2.  **Trazado**: Mediante clics en el canvas SVG, se definen los vértices del área.
3.  **Cierre**: El sistema cierra el polígono automáticamente al detectar el retorno al punto de origen.
4.  **Persistencia**: Se genera el string de coordenadas que alimenta el motor de mapas.

---

## 2. Interfaz de Usuario (EIU)

*   **Lienzo Interactivo**: Canvas SVG con soporte para zoom y pan (desplazamiento).
*   **Estado de Inventario**: Panel lateral con indicadores visuales:
    *   ⚪ **Sin Polígono**: Requiere atención inmediata.
    *   🔵 **Geolocalizado**: Listo para visualización en el mapa de ventas.

> [!TIP]
> Para lograr una precisión milimétrica, se recomienda realizar el trazado con el zoom al 150%. El sistema suaviza automáticamente los bordes para mejorar el rendimiento del navegador.
