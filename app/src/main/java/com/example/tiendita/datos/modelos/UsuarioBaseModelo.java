package com.example.tiendita.datos.modelos;

public abstract class UsuarioBaseModelo {
   protected String id;
   protected String nombre;
   protected String apellido;
   protected String correo;
   protected String contrasenia;
   private String localImg;
   private String remoteImg;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getNombre() {
      return nombre;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public String getApellido() {
      return apellido;
   }

   public void setApellido(String apellido) {
      this.apellido = apellido;
   }

   public String getCorreo() {
      return correo;
   }

   public void setCorreo(String correo) {
      this.correo = correo;
   }

   public String getContrasenia() {
      return contrasenia;
   }

   public void setContrasenia(String contrasenia) {
      this.contrasenia = contrasenia;
   }

   public String getLocalImg() {
      return localImg;
   }

   public void setLocalImg(String localImg) {
      this.localImg = localImg;
   }

   public String getRemoteImg() {
      return remoteImg;
   }

   public void setRemoteImg(String remoteImg) {
      this.remoteImg = remoteImg;
   }
}
