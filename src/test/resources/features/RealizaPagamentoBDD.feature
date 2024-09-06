# language: pt

Funcionalidade: Teste de realização de pagamento

  Cenário: Realiza pagamento com sucesso
    Dado que tenho um carrinho pronto para ser finalizado
    Quando realizo o pagamento
    Entao recebo uma resposta que o pagamento foi realizado com sucesso

  Cenário: Realiza pagamento com carrinho já finalizado ou carrinho que não existe
    Dado que tenho um carrinho já finalizado ou um carrinho que não existe
    Quando realizo o pagamento
    Entao recebo uma resposta que o pagamento não foi realizado

  Cenário: Realiza pagamento com problema na finalização do carrinho
    Dado que tenho um carrinho com problema na finalização
    Quando realizo o pagamento
    Entao recebo uma resposta que o pagamento não foi realizado

  Cenário: Realiza pagamento com usuário que não existe no sistema
    Dado que tenho um carrinho para ser finalizado com um usuário que não existe no sistema
    Quando realizo o pagamento
    Entao recebo uma resposta que o pagamento não foi realizado
