// Aguarda até que todo o conteúdo do DOM  (estrutura HTML da página) seja carregado
document.addEventListener('DOMContentLoaded', () => {
    // Obtém o caminho atual da URL (exemplo: "/adminlte" ou "/kero/subpage")
    const currentPath = window.location.pathname;

    // Seleciona todos os links (<a>) dentro do menu lateral
    const sidebarLinks = document.querySelectorAll('.vertical-nav-menu a');

    // Itera sobre todos os links do menu
    sidebarLinks.forEach(link => {
        // Obtém o valor do atributo 'href' do link (exemplo: "/adminlte")
        const linkPath = link.getAttribute('href');

        // Verifica se a URL atual começa com o valor do 'href' do link
        // Isso cobre rotas exatas e subrotas (exemplo: "/kero" e "/kero/subpage")
        if (currentPath.startsWith(linkPath)) {
            // Adiciona a classe 'mm-active' ao link para destacá-lo como ativo
            link.classList.add('mm-active');

            // Define a variável 'parent' como o elemento pai imediato do link
            let parent = link.parentElement;
			
            // Subir na hierarquia do DOM para ativar elementos pai relacionados
            while (parent && !parent.classList.contains('vertical-nav-menu')) {
                // Se o pai for uma <ul>, adiciona a classe 'mm-show' para abrir o submenu
                if (parent.tagName === 'UL') {
                    parent.classList.add('mm-show');
                }
                // Se o pai for um <li>, adiciona a classe 'mm-active' para marcá-lo como ativo
                if (parent.tagName === 'LI') {
                    parent.classList.add('mm-active');
                }
                // Move para o próximo elemento pai
                parent = parent.parentElement;
            }
        }
    });
});
