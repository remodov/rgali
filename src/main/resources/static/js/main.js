function dropdownCatalogMenu() {
    document.getElementById("prefooter").classList.remove("footer-top-offset-library"); //удаление текущего отступа от футера для раскрытого меню библиотеки
    document.getElementById("prefooter").classList.remove("footer-top-offset-guide"); //удаление текущего отступа от футера для раскрытого меню путеводителя
    document.getElementById("prefooter").classList.remove("footer-top-offset-editions"); //удаление текущего отступа от футера для раскрытого меню изданий

    if (document.getElementById("dropdown-menu-library").classList.toggle("show")) //проверка, раскрыто ли подменю библиотеки (включено ли раскрытие подменю каталога)
    {
        document.getElementById("dropdown-menu-library").classList.toggle("show"); //скрытие подменю библиотеки
    }
    if (document.getElementById("dropdown-menu-guide").classList.toggle("show")) //проверка, раскрыто ли подменю путеводителя (включено ли раскрытие подменю путеводителя)
    {
        document.getElementById("dropdown-menu-guide").classList.toggle("show"); //скрытие подменю путеводителя
    }
    if (document.getElementById("dropdown-submenu-collection").classList.toggle("show")) //проверка, раскрыто ли подменю изданий (включено ли раскрытие подменю путеводителя)
    {
        document.getElementById("dropdown-submenu-collection").classList.toggle("show"); //скрытие подменю изданий
    }

    document.getElementById("dropdown-menu-catalog").classList.toggle("show"); //раскрытие-закрытие подменю для каталога

    document.getElementById("left-menu-item-1").classList.toggle("menu-element-top-offset-catalog"); //переключение отступов для меню1
    document.getElementById("left-menu-item-2").classList.toggle("menu-element-top-offset-catalog"); //переключение отступов для меню2
    document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-catalog"); //переключение отступов для меню3
    document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-catalog"); //переключение отступов для меню4
    document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-catalog"); //переключение отступов для меню5
    document.getElementById("prefooter").classList.toggle("footer-top-offset-catalog"); //включение отступа до футера для раскрытого меню каталога


//отключение отступов для меню, находящихся выше для случаев, если отступы включены (сперва - проверка, включены ли отступы)
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-guide");
    }

    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-guide");
    }

    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-guide");
    }

//отключение ОТСУТСТВИЯ отступов для меню, находящихся ниже для случаев, если ОТСУТСТВИЕ отступов включено (сперва - проверка, включено ли ОТСУТСТВИЕ отступов)
    if (document.getElementById("left-menu-item-1").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-1").classList.toggle("menu-element-no-top-offset");
    }
    if (document.getElementById("left-menu-item-2").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-2").classList.toggle("menu-element-no-top-offset");
    }
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-no-top-offset");
    }
    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-no-top-offset");
    }
    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-no-top-offset");
    }
}

function dropdownGuideMenu() {
    document.getElementById("prefooter").classList.remove("footer-top-offset-catalog"); //удаление текущего отступа от футера для раскрытого меню каталога
    document.getElementById("prefooter").classList.remove("footer-top-offset-library"); //удаление текущего отступа от футера для раскрытого меню библиотеки
    document.getElementById("prefooter").classList.remove("footer-top-offset-editions"); //удаление текущего отступа от футера для раскрытого меню изданий

    if (document.getElementById("dropdown-menu-catalog").classList.toggle("show")) //проверка, раскрыто ли подменю каталога (включено ли раскрытие подменю каталога)
    {
        document.getElementById("dropdown-menu-catalog").classList.toggle("show"); //скрытие подменю каталога
    }
    if (document.getElementById("dropdown-menu-library").classList.toggle("show")) //проверка, раскрыто ли подменю библиотеки (включено ли раскрытие подменю библиотеки)
    {
        document.getElementById("dropdown-menu-library").classList.toggle("show"); //скрытие подменю библиотеки
    }
    if (document.getElementById("dropdown-submenu-collection").classList.toggle("show")) //проверка, раскрыто ли подменю изданий (включено ли раскрытие подменю путеводителя)
    {
        document.getElementById("dropdown-submenu-collection").classList.toggle("show"); //скрытие подменю изданий
    }


    //отключение отступов для каталога:
    if (document.getElementById("left-menu-item-1").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-1").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-2").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-2").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-catalog")
    }


    document.getElementById("dropdown-menu-guide").classList.toggle("show"); //раскрытие-закрытие подменю для путеводителя

    document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-guide"); //переключение отступов для меню3
    document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-guide"); //переключение отступов для меню4
    document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-guide"); //переключение отступов для меню5
    document.getElementById("prefooter").classList.toggle("footer-top-offset-guide"); //включение отступа до футера для раскрытого меню путеводителя


//отключение ОТСУТСТВИЯ отступов для меню, находящихся ниже для случаев, если ОТСУТСТВИЕ отступов включено (сперва - проверка, включено ли ОТСУТСТВИЕ отступов)
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-no-top-offset");
    }

    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-no-top-offset");
    }

    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-no-top-offset"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-no-top-offset");
    }
}

function dropdownLibraryMenu() {
    document.getElementById("prefooter").classList.remove("footer-top-offset-catalog"); //удаление текущего отступа от футера для раскрытого меню каталога
    document.getElementById("prefooter").classList.remove("footer-top-offset-guide"); //удаление текущего отступа от футера для раскрытого меню путеводителя
    document.getElementById("prefooter").classList.remove("footer-top-offset-editions"); //удаление текущего отступа от футера для раскрытого меню изданий


    //отключение отступов для каталога:
    if (document.getElementById("left-menu-item-1").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-1").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-2").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-2").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-catalog")
    }



    if (document.getElementById("dropdown-menu-catalog").classList.toggle("show")) //проверка, раскрыто ли подменю каталога (включено ли раскрытие подменю каталога)
    {
        document.getElementById("dropdown-menu-catalog").classList.toggle("show"); //скрытие подменю каталога
    }
    if (document.getElementById("dropdown-menu-guide").classList.toggle("show")) //проверка, раскрыто ли подменю путеводителя (включено ли раскрытие подменю путеводителя)
    {
        document.getElementById("dropdown-menu-guide").classList.toggle("show"); //скрытие подменю путеводителя
    }
    if (document.getElementById("dropdown-submenu-collection").classList.toggle("show")) //проверка, раскрыто ли подменю изданий (включено ли раскрытие подменю издания)
    {
        document.getElementById("dropdown-submenu-collection").classList.toggle("show"); //скрытие подменю изданий
    }

    document.getElementById("dropdown-menu-library").classList.toggle("show"); //раскрытие-закрытие подменю для библиотеки


    document.getElementById("left-menu-item-3").classList.toggle("menu-element-no-top-offset"); //переключение ОТСУТСТВИЯ отступов для меню3
    document.getElementById("left-menu-item-4").classList.toggle("menu-element-no-top-offset"); //переключение ОТСУТСТВИЯ отступов для меню3
    document.getElementById("left-menu-item-5").classList.toggle("menu-element-no-top-offset"); //переключение ОТСУТСТВИЯ отступов для меню3
    document.getElementById("prefooter").classList.toggle("footer-top-offset-library"); //включение отступа до футера для раскрытого меню библиотеки

//отключение отступов для меню, находящихся выше для случаев, если отступы включены (сперва - проверка, включены ли отступы)
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-guide");
    }

    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-guide");
    }

    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-guide");
    }
}

function dropdownEditionsMenu() {
    document.getElementById("prefooter").classList.remove("footer-top-offset-catalog"); //удаление текущего отступа от футера для раскрытого меню каталога
    document.getElementById("prefooter").classList.remove("footer-top-offset-guide"); //удаление текущего отступа от футера для раскрытого меню путеводителя

    //отключение отступов для каталога:
    if (document.getElementById("left-menu-item-1").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-1").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-2").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-2").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-catalog")
    }
    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-catalog"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-catalog")
    }


    if (document.getElementById("dropdown-menu-catalog").classList.toggle("show")) //проверка, раскрыто ли подменю каталога (включено ли раскрытие подменю каталога)
    {
        document.getElementById("dropdown-menu-catalog").classList.toggle("show"); //скрытие подменю каталога
    }
    if (document.getElementById("dropdown-menu-guide").classList.toggle("show")) //проверка, раскрыто ли подменю путеводителя (включено ли раскрытие подменю путеводителя)
    {
        document.getElementById("dropdown-menu-guide").classList.toggle("show"); //скрытие подменю путеводителя
    }

    document.getElementById("dropdown-submenu-collection").classList.toggle("show"); //раскрытие-закрытие подменю для изданий

    document.getElementById("left-menu-item-3").classList.toggle("menu-element-no-top-offset"); //переключение ОТСУТСТВИЯ отступов для меню3
    document.getElementById("left-menu-item-4").classList.toggle("menu-element-no-top-offset"); //переключение ОТСУТСТВИЯ отступов для меню3
    document.getElementById("left-menu-item-5").classList.toggle("menu-element-no-top-offset"); //переключение ОТСУТСТВИЯ отступов для меню3
    document.getElementById("prefooter").classList.toggle("footer-top-offset-editions"); //включение отступа до футера для раскрытого меню изданий

//отключение отступов для меню, находящихся выше для случаев, если отступы включены (сперва - проверка, включены ли отступы)
    if (document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-3").classList.toggle("menu-element-top-offset-guide");
    }

    if (document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-4").classList.toggle("menu-element-top-offset-guide");
    }

    if (document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-guide"))
    {
        document.getElementById("left-menu-item-5").classList.toggle("menu-element-top-offset-guide");
    }
}

function Previous() {
    window.history.back()
}

$(document).ready(function() {
  var currentMenuId = $("#currentFundType").text()
  if (currentMenuId !== '' && currentMenuId !== undefined) {
      $("#" + currentMenuId + "-text").css('color', '#be1e2d');

      if (currentMenuId !== 'all') {
        document.getElementById('left-menu-item-2').click()
      }
  }
});

function startSimpleSearch() {
       document.getElementById('organizationName').value = document.getElementById('simpleSearch').value
       document.getElementById('personaName').value = document.getElementById('simpleSearch').value
       document.getElementById('fondName').value = document.getElementById('simpleSearch').value
       document.getElementById('isSimpleSearch').value = "true"

       document.getElementById('searchFormShort').submit()
}

function setSelectedPerson(elem) {
    $('#searchPersonaName').val(elem)
}

function commonStorageUnitSearch() {
   $('#currentPageField').val("1")
   $('#elementsOnPageField').val("20")
   $('#fieldForSortField').val("")
   $('#searchForm').attr('action', "/storage-unit/search").submit();
}

function commonFundSearch() {
    $('#currentPageField').val("1")
    $('#elementsOnPageField').val("20")
    $('#fieldForSortField').val("")
    $('#searchForm').attr('action', "/fund/search").submit();
}

function commonPersonSearch() {
   $('#searchForm').attr('action', "/person/search").submit();
}

function commonOrganizationSearch() {
   $('#searchForm').attr('action', "/organization/search").submit();
}

//маска для поля ввода номера телефона
function phoneMask() {
    const tel = $('[type="tel"]');

    tel.keydown(function (e) {
      if (!tel.val().startsWith('+'))
        tel.val().length == 0 ? tel.val('+7' + tel.val()) : tel.val('+' + tel.val());
    })
    .keyup(function () {
      tel.val('+' + tel.val().split("+").pop());
      if (!tel.val().startsWith('+')) tel.val('+' + tel.val());
    })
    .change(function() {
      if (tel.val() === '+') tel.val('');
    });
}