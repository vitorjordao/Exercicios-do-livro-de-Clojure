(defn resumo [transacao] 
    (select-keys transacao [:valor :tipo :data]))

(def transacoes [
    {:valor 33.3 :tipo "despesa" :comentario "Almoço" :data "19/11/2016"}
    {:valor 2700.0 :tipo "receita" :comentario "Bico" :data "01/12/2016"}   
    {:valor 29.0 :tipo "despesa" :comentario "Livro de Clojure" :data "03/12/2016"}])

(map resumo transacoes)

(defn despesa? [transacao]
  (= (:tipo transacao) "despesa"))

(filter despesa? transacoes)

(defn so-valor [transacao]
  (:valor transacao))

;;thread-first
(-> (first transacoes)
    (so-valor))

;; Isso:
(reduce + (map so-valor (filter despesa? transacoes)))
;; é o mesmo que isso:
(reduce + 
        (map #(:valor %) 
             (filter #(= (:tipo %) "despesa") 
                     transacoes)))

;; Isso:
(filter (fn [transacao]
          (> (:valor transacao) 100))
        transacoes)
;; é o mesmo que isso:
(filter #(> (:valor %) 100) transacoes)

;; thread-last
(->> (filter despesa? transacoes)
     (map so-valor)
     (reduce +))

(def transacoes  [{:valor 33.0 :tipo "despesa" :comentario "Almoço" 
                   :moeda "R$" :data "19/11/2016"} 
                  {:valor 2700.0 :tipo "receita" :comentario "Bico" 
                   :moeda "R$" :data "01/12/2016"} 
                  {:valor 29.0 :tipo "despesa" :comentario "Livro de Clojure" 
                   :moeda "R$" :data "03/12/2016"}])

;; Isso:
(def ola (fn [nome] (str "Olá, " nome "!")))
(ola "mundo novo")
;; É o mesmo que isso:
(defn ola [nome] (str "Olá, " nome "!"))
(ola "mundo novo")
;; defn é uma macro de def + fn

;; Isso:
(defn valor-sinalizado [transacao] 
  (if (= (:tipo transacao) "despesa") 
    (str (:moeda transacao) " -" (:valor transacao))
    (str (:moeda transacao) " +" (:valor transacao))))
;; É o mesmo que isso:
(defn valor-sinalizado [transacao]
  (let [moeda (:moeda transacao)
        valor (:valor transacao)]
    (if (= (:tipo transacao) "despesa")
        (str moeda " -" valor)
        (str moeda " +" valor))))


(map valor-sinalizado transacoes)

;; First pega o primeiro elemento
(valor-sinalizado (first transacoes))
;; Second pega o segundo elemento
(valor-sinalizado (second transacoes))

(def transacao-aleatoria {:valor 9.0}) 

(valor-sinalizado transacao-aleatoria)
;; " +9.0"

(:moeda transacao-aleatoria)
;; nil

(:moeda transacao-aleatoria "R$")
;; "R$"

(defn valor-sinalizado [transacao]
  (let [moeda (:moeda transacao "R$")
        valor (:valor transacao)]
    (if (= (:tipo transacao) "despesa")
      (str moeda " -" valor)
      (str moeda " +" valor))))

(valor-sinalizado transacao-aleatoria)

(defn data-valor [transacao]
  (str (:data transacao) " => " (valor-sinalizado transacao)))

(map data-valor transacoes)

(defn transacao-em-yuan [transacao] 
  (assoc transacao :valor (* 2.15 (:valor transacao))
         :moeda "¥"))

(map transacao-em-yuan transacoes)

(def cotacoes 
  {:yuan {:cotacao 2.15 :simbolo "¥"}})

;; Isso
(defn transacao-em-yuan [transacao]
  (assoc transacao :valor (* (:cotacao (:yuan cotacoes))
                             (:valor transacao))
         :moeda (:simbolo (:yuan cotacoes))))
;; É o mesmo que isso:
(defn transacao-em-yuan [transacao]
  (assoc transacao :valor (* (get-in cotacoes [:yuan :cotacao])
                             (:valor transacao))
         :moeda (get-in cotacoes [:yuan :simbolo])))
;; Que é o mesmo que isso:
(defn transacao-em-yuan [transacao]
  (let [yuan (:yuan cotacoes)]
    (assoc transacao :valor (* (:cotacao yuan) (:valor transacao))
                               :moeda (:simbolo yuan))))


(transacao-em-yuan (first transacoes))

(data-valor (first transacoes))

(data-valor (transacao-em-yuan (first transacoes)))

(class 3.1)
;; java.lang.Double
(* 3.1 3.1)
;; 9.610000000000001
(class 3.1M)
;; java.math.BigDecimal
(* 3.1M 3.1)
;; 9.610000000000001
(* 3.1M 3.1M)
;; 9.61M
(class (* 3.1M 3.1M))
;; java.math.BigDecimal

;; Esse "M" na frente dos números transforma eles em BigDecimal 

(def cotacoes 
  {:yuan {:cotacao 2.15M :simbolo "¥"}})

(def transacoes
  [{:valor 33.0M :tipo "despesa" :comentario "Almoço" 
    :moeda "R$" :data "19/11/2016"}
   {:valor 2700.0M :tipo "receita" :comentario "Bico" 
    :moeda "R$" :data "01/12/2016"}
   {:valor 29.0M :tipo "despesa" :comentario "Livro de Clojure" 
    :moeda "R$" :data "03/12/2016"}])

;; Isso
(defn texto-resumo-em-yuan [transacao]
  (data-valor (transacao-em-yuan transacao)))
;; É o mesmo que isso:
(defn texto-resumo-em-yuan [transacao]
  (-> (transacao-em-yuan transacao)
      (data-valor)))
;; Que é o mesmo quee isso:
(def texto-resumo-em-yuan (comp data-valor transacao-em-yuan))

(map texto-resumo-em-yuan transacoes)

;; Isso
(defn transacao-em-yuan [transacao]
  (let [yuan (:yuan cotacoes)]
    (assoc transacao :valor (* (:cotacao yuan) (:valor transacao))
           :moeda (:simbolo yuan))))
;; É o mesmo que isso:
(defn transacao-em-yuan [transacao]
  (let [{yuan :yuan} cotacoes]
    (assoc transacao :valor (* (:cotacao yuan) (:valor transacao))
           :moeda (:simbolo yuan))))
;; Que é o mesmo que isso:
(defn transacao-em-yuan [transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} :yuan} cotacoes]
    (assoc transacao :valor (* cotacao (:valor transacao))
           :moeda simbolo)))
(map transacao-em-yuan transacoes)

(def cotacoes
  {:yuan {:cotacao 2.15M :simbolo "¥"}
  :euro {:cotacao 0.28M :simbolo "€"}})

(defn transacao-em-outra-moeda [moeda transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes]
    (assoc transacao :valor (* cotacao (:valor transacao))
           :moeda simbolo)))

(defn transacao-em-outra-moeda [moeda transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes]
    (assoc transacao :valor (* cotacao (:valor transacao))
           :moeda simbolo)))

(transacao-em-outra-moeda :euro (last transacoes))

;;E é uma aplicação parcial porque declaramos parte dos argumentos, não todos. A outra parte do argumento virá em algum outro momento, quando a função for, de fato, aplicada
(def transacao-em-yuan (partial transacao-em-outra-moeda :yuan))

(def transacao-em-euro (partial transacao-em-outra-moeda :euro))

(transacao-em-euro (first transacoes))

(transacao-em-yuan (first transacoes))

(map transacao-em-yuan transacoes)

(clojure.string/join ", " (map transacao-em-yuan transacoes))

(def junta-tudo (partial clojure.string/join ", "))

(junta-tudo (map transacao-em-yuan transacoes))

(def de-para [{:de "a" :para "4"}
              :de "e" :para "3"
              :de "i" :para "1"
              :de "o" :para "0"])

(defn escrita-hacker [texto dicionario]
  (if (empty? dicionario)
    texto
    (let [conversao (first dicionario)]
      (escrita-hacker (clojure.string/replace texto
                                              (:de conversao)
                                              (:para conversao))
                      (rest dicionario)))))


(escrita-hacker "alameda" de-para)


(defn transacao-em-outra-moeda [cotacoes moeda transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes]
    (assoc transacao :valor (* cotacao (:valor transacao))
           :moeda simbolo)))

(transacao-em-outra-moeda cotacoes :euro (last transacoes))

(defn transacao-convertida [cotacoes moeda transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes] 
    (assoc transacao :valor (* cotacao (:valor transacao))
           :moeda simbolo)))

(def transacao-em-outra-moeda 
  (partial transacao-convertida cotacoes))

(transacao-em-outra-moeda :euro (last transacoes))


(defn transacao-em-outra-moeda 
  ([cotacoes moeda transacao]
   (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes]
     (assoc transacao :valor (* cotacao (:valor transacao))
            :moeda simbolo)))
  ([moeda transacao]
   (transacao-em-outra-moeda cotacoes moeda transacao)))

(transacao-em-outra-moeda :euro (last transacoes))

(def membros-fundadores 
  (list "Argentina" "Brasil" "Paraguai" "Uruguai"))

(def membros-plenos 
  (cons "Venezuela" membros-fundadores))

;; rest pega uma lista e retorna ela sem o primeiro elemento
(rest membros-plenos)

;; identical? é uma função que compara 2 elementos para
;; saber se são os mesmos objetos
(identical? (rest membros-plenos)
            membros-fundadores)


(def transacoes 
  [{:valor 33M :tipo "despesa" :comentario "Almoço"
    :moeda "R$" :data "19/11/2016"}
   {:valor 2700M :tipo "receita" :comentario "Bico"
    :moeda "R$" :data "01/12/2016"}
   {:valor 29M :tipo "despesa" :comentario "Livro de Clojure"
    :moeda "R$" :data "03/12/2016"}])

(def transacoes (cons {:valor 45M :tipo "despesa"
                       :comentario "Jogo no Steam" :moeda "R$"
                       :data "26/12/2016"}
                      transacoes))

(def registros (atom ()))

(swap! registros conj {:valor 29M :tipo "despesa"
                      :comentario "Livro de clojure" :moeda "R$"
                      :data "03/12/2016"})

(conj {:valor 29M :tipo "despesa" :comentario "Livro de Clorure"
       :moeda "R$" :data "03/12/2016"})

(swap! registros conj
       {:valor 2700M :tipo "receita" :comentario "Bico"
        :moeda "R$" :data "01/12/2016"})

(defn registrar [transacao] 
  (swap! registros conj transacao))

(registrar {:valor 33M :tipo "despesa" :comentario "Almoço"
  :moeda "R$" :data "19/11/2016"})

(registrar {:valor 45M :tipo "despesa" :comentario "Jogo no Steam"
  :moeda "R$" :data "26/12/2016"})

(def transacoes @registros)
transacoes

(defn saldo-acumulado [acumulado transacoes]
  ;; if-let
  (if-let [transacao (first transacoes)]
    ;; se _transacao_ existir, continue calculando o saldo
    (saldo-acumulado (if (despesa? transacao)
                          (- acumulado (:valor transacao))
                          (+ acumulado (:valor transacao)))
                      (rest transacoes))
    ;; se não existir, a coleção de transacoes acabou e é hora de
    ;; retornar o resultado
    acumulado))

  (saldo-acumulado 0 transacoes) 

;; e qual seria o resultado com uma lista sem transações?
(saldo-acumulado 0 ())

;; ou só os dois primeiros elementos?
(saldo-acumulado 0 (take 2 transacoes))

(defn calcular [acumulado transacao]
  (let [valor (:valor transacao)]
    (if (despesa? transacao)
      (- acumulado valor)
      (+ acumulado valor))))
  
(defn saldo-acumulado [acumulado transacoes]
  (if-let [transacao (first transacoes)]
    (saldo-acumulado (calcular acumulado transacao)
                      (rest transacoes))
  acumulado))

  (defn saldo-acumulado [acumulado transacoes]
    (if-let [transacao (first transacoes)]
      (do
        (prn "Começou saldo-acumulado. Saldo até agora:" acumulado)
        (saldo-acumulado (calcular acumulado transacao)
                          (rest transacoes)))
      (do
        (prn "Processo encerrado. Saldo final:" acumulado)
        acumulado)))

(saldo-acumulado 0 transacoes)

(defn saldo-acumulado [acumulado transacoes]
  (prn "Começou saldo-acumulado. Saldo até agora:" acumulado)
  (if-let [transacao (first transacoes)]
    (do
      (prn "Valor da transação atual:"
        (valor-sinalizado transacao))
      (prn "Quantidade de transações restantes:"
        (count (rest transacoes)))
      (prn)
      (saldo-acumulado (calcular acumulado transacao)
                        (rest transacoes)))
    (do
      (prn "Processo encerrado. Saldo final:" acumulado)
      acumulado)))

(saldo-acumulado 0 transacoes)

(defn saldo-acumulado [acumulado transacoes]
  (if-let [transacao (first transacoes)]
    (saldo-acumulado (calcular acumulado transacao)
                      (rest transacoes))
  acumulado))

(defn saldo 
  ([transacoes] 
   (saldo 0 transacoes))
  ([acumulado transacoes]
   (if-let [transacao (first transacoes)]
     (saldo (calcular acumulado transacao)
                      (rest transacoes))
     acumulado)))

(saldo transacoes)

(saldo 0 transacoes)

(defn como-transacao [valor]
  {:valor valor})

(def poucas-transacoes
  (map como-transacao (range 10)))

(def muitas-transacoes
  (map como-transacao (range 1000)))

(def incontaveis-transacoes
  (map como-transacao (range 100000)))

(saldo poucas-transacoes)
(saldo muitas-transacoes)
(saldo incontaveis-transacoes)


;; Com otimização em cauda
(defn saldo
  ([transacoes]
   (saldo 0 transacoes))
  ([acumulado transacoes]
   (if (empty? transacoes)
     acumulado
     (saldo (calcular acumulado (first transacoes))
                      (rest transacoes)))))

(saldo poucas-transacoes)
(saldo muitas-transacoes)
(saldo incontaveis-transacoes)

;; Com otimização em cauda
(defn saldo
  ([transacoes]
   (saldo 0 transacoes))
  ([acumulado transacoes]
   (if (empty? transacoes)
     acumulado
;;    vvvvv aqui utilizamos recur em vez de saldo para aplicar a otimização
     (recur (calcular acumulado (first transacoes))
            (rest transacoes)))))

(saldo poucas-transacoes)
(saldo muitas-transacoes)
(saldo incontaveis-transacoes)

;;Tem como usar reduce
(reduce calcular 0 transacoes)



(defn teste-da-preguica []
  (prn "Não deveria aparecer nada aqui")
  "nada")

;; recebe dois argumentos
;; mas não faz nada com eles
;; retornando uma string
(defn um-oi [a b]
  "oi")

(um-oi (teste-da-preguica) (teste-da-preguica))


(def transacoes
  [{:valor 33.0 :tipo "despesa" :comentario "Almoço"
    :moeda "R$" :data "19/11/2016"}
   {:valor 2700.0 :tipo "receita" :comentario "Bico"
    :moeda "R$" :data "01/12/2016"}
   {:valor 29.0 :tipo "despesa" :comentario "Livro de Clojure"
    :moeda "R$" :data "03/12/2016"}
   {:valor 45M :tipo "despesa" :comentario "Jogo no Steam"
    :moeda "R$" :data "26/12/2016"}])
;; uma função que nos diz se uma transação é uma despesa
(defn despesa? [transacao]
  (= (:tipo transacao) "despesa"))
;; vamos pegar uma sequência que só tenha despesas
(filter despesa? transacoes)


(def despesas (filter despesa? transacoes))
despesas

(def transacao-aleatoria {:valor 9.0})

(valor-sinalizado transacao-aleatoria)

(def valores (map valor-sinalizado transacoes))
valores

;; rand-nth retorna um valor aleatório dentro de uma coleção
(rand-nth ["despesa" "receita"])

;; rand-int retorna um número inteiro aleatório entre 0 e
;; o argumento, sem incluir o argumento nas possibilidades
;; daí multiplicamos por 0.01M para ter um número real com
;; duas casas decimais
(* (rand-int 100001) 0.01M)

(defn transacao-aleatoria []
  {:valor (* (rand-int 100001) 0.01M)
   :tipo (rand-nth ["despesa" "receita"])})
(transacao-aleatoria)

;; repeatedly produz uma sequência preguiçosa, cujos elementos são
;; chamadas à função que lhe é passada como argumento
(repeatedly 3 transacao-aleatoria)
;; ... uma lista com 3 transações aleatórias
(class (repeatedly 3 transacao-aleatoria))
;; quando não dizemos quantos elementos queremos, repeatedly cria
;; uma sequência infinita
(def transacoes-aleatorias (repeatedly transacao-aleatoria))


(take 1 transacoes-aleatorias)

(take 2 transacoes-aleatorias)

(take 5 transacoes-aleatorias)

(= (take 5 transacoes-aleatorias) (take 5 transacoes-aleatorias))

(cons (transacao-aleatoria) transacoes)

(defn aleatorias
  ([quantidade]
   (aleatorias quantidade 1 (list (transacao-aleatoria))))
  ([quantidade quantas-ja-foram transacoes]
   (if (< quantas-ja-foram quantidade)
     (aleatorias quantidade (inc quantas-ja-foram)
                 (cons (transacao-aleatoria) transacoes))
     transacoes)))

(aleatorias 4)

;;erro
(aleatorias 900000)

(defn aleatorias
  ([quantidade]
   (aleatorias quantidade 1 (list (transacao-aleatoria))))
  ([quantidade quantas-ja-foram transacoes]
   (if (= quantas-ja-foram quantidade)
     transacoes
     (recur quantidade (inc quantas-ja-foram)
                 (cons (transacao-aleatoria) transacoes)))))
;; Agora vai! (mas demora)
;; (aleatorias 900000)

(class (aleatorias 4))
;; clojure.lang.Cons
(class (aleatorias 900000))
;; clojure.lang.Cons

(time (class (aleatorias 4)))
;; "Elapsed time: 0.054824 msecs"
;; clojure.lang.Cons
(time (class (aleatorias 900000)))
;; "Elapsed time: 917.45872 msecs"
;; clojure.lang.Cons

(defn aleatorias
  ([quantidade]
   (aleatorias quantidade 1 (list (transacao-aleatoria))))
  ([quantidade quantas-ja-foram transacoes]
   (lazy-seq
    (if (= quantas-ja-foram quantidade)
      transacoes
      (aleatorias quantidade (inc quantas-ja-foram)
             (cons (transacao-aleatoria) transacoes))))))

(time (class (aleatorias 4)))
;; "Elapsed time: 0.03271 msecs"
;; clojure.lang.LazySeq
(time (class (aleatorias 900000)))
;; "Elapsed time: 0.029649 msecs"
;; clojure.lang.LazySeq

(defn aleatorias []
  (lazy-seq
   (cons (transacao-aleatoria) (aleatorias))))

(time (class (take 4 (aleatorias))))
;; "Elapsed time: 0.170615 msecs"
;; clojure.lang.LazySeq
(time (class (take 900000 (aleatorias))))
;; "Elapsed time: 0.046173 msecs"
;; clojure.lang.LazySeq

(take 4 (aleatorias))

;;Funciona sem estourar a pilha (mas demora) graças a lazy-seq
;;(take 900000 (aleatorias))

(require '[clj-http.client :as http-client])

(http-client/get "https://pokeapi.co/api/v2/pokemon/pikachu/")

(def chave (System/getenv "CHAVE_API"))

(def api-url 
  "https://free.currencyconverterapi.com/api/v6/convert")

(http-client/get api-url 
  {:query-params {"q" "USD_BRL"
                  "apiKey" chave}})

(http-client/get api-url
  {:query-params {"q" "EUR_BRL"
                  "apiKey" chave}})


(require '[cheshire.core :refer :all])
(require '[clj-http.client :as http-client])

(def usd-brl
  (:body (http-client/get api-url
  {:query-params {"q" "USD_BRL"
  "apiKey" chave}})))

(parse-string usd-brl)

(-> (parse-string usd-brl)
    (get-in ["results" "USD_BRL" "val"]))