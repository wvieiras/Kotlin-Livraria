package br.com.zup.autores

data class DetalhesDoAutorResponse(val autor: Autor) {

    val nome = autor.nome
    val email = autor.email
    val descricao = autor.descricao
}
