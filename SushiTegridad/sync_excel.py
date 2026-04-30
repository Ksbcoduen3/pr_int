import pandas as pd
import os
import sys

# Forzar salida en UTF-8 para soportar emojis y caracteres especiales
sys.stdout.reconfigure(encoding='utf-8')

# Ruta al archivo Excel
archivo_excel = 'SushiTegridad_Analytics.xlsx'

# Verificar que el archivo exista antes de intentar abrirlo
if not os.path.exists(archivo_excel):
    print(f"  [INFO] El archivo '{archivo_excel}' aun no existe. Se creara cuando haya datos para exportar.")
    sys.exit(0)

# 1. Cargar el archivo completo (esto te permite ver todas las hojas)
xls = pd.ExcelFile(archivo_excel)
print("Hojas disponibles:", xls.sheet_names)

# Funcion auxiliar para buscar una hoja por nombre parcial (ignora emojis/prefijos)
def buscar_hoja(nombre_parcial):
    for hoja in xls.sheet_names:
        if nombre_parcial.lower() in hoja.lower():
            return hoja
    return None

# 2. Leer hoja de Almacén
hoja_almacen = buscar_hoja('Almacen')
if hoja_almacen:
    df_almacen = pd.read_excel(archivo_excel, sheet_name=hoja_almacen, skiprows=1)
    print(f"\n--- Datos de '{hoja_almacen}' ---")
    print(df_almacen.head())

# 3. Leer hoja de Ventas
hoja_ventas = buscar_hoja('Ventas')
if hoja_ventas:
    df_ventas = pd.read_excel(archivo_excel, sheet_name=hoja_ventas, skiprows=1)
    print(f"\n--- Datos de '{hoja_ventas}' ---")
    print(df_ventas.head())
else:
    print("\n[AVISO] No se encontro la hoja de Ventas.")

print("\n  [OK] Sincronizacion completada.")