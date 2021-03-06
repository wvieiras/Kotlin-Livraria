package br.com.zup.autores

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.notFound
import io.micronaut.http.HttpResponse.ok
import io.micronaut.http.annotation.*
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/autores")
class CadastrarAutorController(val autorRepository: AutorRepository) {

    @Post
    @Transactional
    fun cadastra(@Body @Valid request: NovoAutorRequest): HttpResponse<Any>{

        val autor = request.toMode()
        autorRepository.save(autor)

        val uri = UriBuilder.of("/autores/{id}")
            .expand(mutableMapOf(Pair("id", autor.id)))

        return HttpResponse.created(uri)

    }

    @Get
    @Transactional
    fun lista(@QueryValue(defaultValue = "") email: String): HttpResponse<Any> {

        if (email.isBlank()) {
            val autores = autorRepository.findAll()

            val resposta = autores.map { autor -> DetalhesDoAutorResponse(autor) }

            return HttpResponse.ok(resposta)
        }
        val possivelAutor = autorRepository.findByEmail(email)

        if (possivelAutor.isEmpty) {
            return HttpResponse.notFound()
        }

        val autor = possivelAutor.get()

        return HttpResponse.ok(DetalhesDoAutorResponse(autor))

    }

    @Put("/{id}")
    @Transactional
    fun put(@PathVariable id: Long, descricao: String): HttpResponse<Any>{

        //buscar o objeto no banco
        val possivelAutor =  autorRepository.findById(id)

        //verifica se existe o id
        if(possivelAutor.isEmpty){
            return HttpResponse.notFound()
        } else {

            //atualizar o campo
            val autor = possivelAutor.get()

            autor.descricao = descricao

            autorRepository.update(autor)

            //retornar ok
            return HttpResponse.ok(DetalhesDoAutorResponse(autor))
        }
    }

    @Delete("/{id}")
    @Transactional
    fun del(@PathVariable id: Long): HttpResponse<Any>{

        val possivelAutor = autorRepository.findById(id)

        if(possivelAutor.isEmpty){
            return HttpResponse.notFound()
        }else{
            autorRepository.deleteById(id)
            return HttpResponse.ok()
        }


    }
}